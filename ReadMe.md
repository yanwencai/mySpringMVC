手写SpringMVC实例代码
====================================
核心知识
-----------------------------
    1.servlet规范
    2.注解以及自定义注解
    3.反射


设计
--------------------------------------
    a.使应用符合mvc结构
    b.定义repository、service、controller、Autowired、RequestMapping注解
    c.使用自定义注解
# 新建一个名为DispatcherServlet的servlet，负责以下职责：
1.扫描基包，把基包下所有的类放到一个List下，之后需要处理这些类中的注解，基包可以写到配置文件中。<br/>
2.遍历List,分别处理类注解,属性注解以及方法注解<br/>
>类注解：如果类注解存在，比如@Repository 那么将类的完整类名作为key(包名+类名)，类的实例作为value保存到一个map中
                Map<com.mvc.dao.TestDao TestDao.newInstance>
        属性注解：如果是属性注解那么就需要注入了，优先处理service层，比如 @Autowired 根据反射原理获得该属性的类型，然后根据类型就可以从Map中获取到类型的实例，
                  然后就可以通过field.set方法注入属性了。

        'Object o = instanceMap.get(field.getType().getName());
        try {
            field.set(entry.getValue(),o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }'

>方法注解：主要处理Controller层中的RequestMapping注解，将注解中的参数作为key，注解的方法的反射对象method作为value，维护到一个map中就可以了。

3.如果是get请求，我们可以在doGet方法中解析请求路径，然后从方法注解中维护出来的map中拿对应的方法，如果拿得到，调用 method.invoke方法。

