package com.mvc.servlet;


import com.mvc.annotations.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTML;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/13.
 */
public class DispatcherServlet extends HttpServlet {
    private List<String> classList = new ArrayList<String>();
    private Map<String, Object> instanceMap = new HashMap<String, Object>();
    private Map<String,Method> methodMap=new HashMap<String, Method>();//方法和requestMapping路径关联关系

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handler(request,response);
    }

    /**
     * 请求分发
     * @param request
     * @param response
     */
    private void handler(HttpServletRequest request, HttpServletResponse response){
        String requestURI = request.getRequestURI();
        String servletPath = request.getServletPath();
        Method method = methodMap.get(servletPath);
        if(method!=null){
            try {
                String cName = method.getDeclaringClass().getName();
                Object o = instanceMap.get(cName);
                method.invoke(o,request,response);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            System.out.println(requestURI);
        }else{
            try {
                response.setCharacterEncoding("utf-8");
                PrintWriter out = response.getWriter();
                out.println("<html>");
                out.println("<body><h2>");
                out.println(request.getServletPath()+"未找到！");
                out.println("</h2></body>");
                out.println("</html>");
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 1.扫描包，
     * 2.将所有注解的类找到,根据注解完成DI
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        scanPackage("com.mvc");
        //注解扫描 类注解 实例化类
        getInstance();
        //属性注解扫描，注入依赖关系
        ioc();
        //请求方法和controller中对应的方法建立关联关系
    }




    /**
     * 依赖注入
     */
    private void ioc(){
        for(Map.Entry<String,Object> entry:instanceMap.entrySet()){
            Class value = entry.getValue().getClass();
            if(value.isAnnotationPresent(Service.class)){
                Field[] fields = value.getDeclaredFields();
                for(Field field:fields){
                    if(field.isAnnotationPresent(Autowired.class)){
                        field.setAccessible(true);
                        System.out.println(field.getType());
                        Object o = instanceMap.get(field.getType().getName());
                        try {
                            field.set(entry.getValue(),o);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }else if(value.isAnnotationPresent(Controller.class)){//controller层注入和requestMapping处理
                Field[] fields = value.getDeclaredFields();
                for(Field field:fields){
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(),instanceMap.get(field.getType().getName()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                Method[] methods = value.getDeclaredMethods();
                for(Method method:methods){
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        String url = "/"+method.getAnnotation(RequestMapping.class).value();
                        methodMap.put(url,method);
                        System.out.println(url);
                    }
                }

            }
        }
        System.out.println("ioc end...");
    }

   /* private void ioc(){
        for(String c:classList){
            try {
                Class<?> aClass = Class.forName(c);
                Field[] fields = aClass.getFields();
                if(aClass.isAnnotationPresent(Service.class)){
                    for(Field f:fields){
                        if(f.isAnnotationPresent(Autowired.class)){
                            f.setAccessible(true);
                            Object o = instanceMap.get(c);

                        }
                    }
                }



            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }*/

    /**
     * 类注解扫描
     * isAnnotationPresent():如果指定类型的注释存在于此元素上返回true, 否则返回false
     */
    private void getInstance() {
        for (String c : classList) {
            try {
                Class<?> aClass = Class.forName(c);
                if(aClass.isAnnotationPresent(Repository.class)){
                    Object instance = aClass.newInstance();
                    instanceMap.put(c,instance);
                }else if(aClass.isAnnotationPresent(Service.class)){
                    Object instance = aClass.newInstance();
                    instanceMap.put(c,instance);
                }else if(aClass.isAnnotationPresent(Controller.class)){
                    Object instance = aClass.newInstance();
                    instanceMap.put(c,instance);
                }
                System.out.println("" + aClass.isAnnotationPresent(Repository.class));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("type annotation scan end...");
    }


    /**
     * 核心方法
     * 包扫描-递归
     * @param basePackage
     */
    private void scanPackage(String basePackage) {
        String packageDir = basePackage.replaceAll("\\.", "/");
        String basePath = Thread.currentThread().getContextClassLoader().getResource(packageDir).getPath();
        File pFile = new File(basePath);
        String[] fileNames = pFile.list();
        for (String f : fileNames) {
            File file = new File(basePath + f.replaceAll(".class", ""));
            if (file.isDirectory()) {
                System.out.println("Directory:" + f);
                scanPackage(basePackage + "." + f);
            } else {
                //classList.add((basePath+f).replaceAll(".class",""));
                classList.add((basePackage + "." + (f.replaceAll(".class", ""))).replaceAll("//", "\\."));
                System.out.println("file:" + f);
            }
        }
    }
}
