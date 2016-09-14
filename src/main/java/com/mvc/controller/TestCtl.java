package com.mvc.controller;

import com.mvc.annotations.Autowired;
import com.mvc.annotations.Controller;
import com.mvc.annotations.RequestMapping;
import com.mvc.service.TestService;
import com.sun.org.apache.xpath.internal.operations.String;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Created by Administrator on 2016/9/13.
 */
@Controller
public class TestCtl {

    @Autowired("testService")
    private TestService testService;

    @RequestMapping("findAll")
    public String findAll(HttpServletRequest request, HttpServletResponse response){
        System.out.println(this.getClass().getName()+":findAll");
        java.lang.String name = request.getParameter("name");
        System.out.println("name:"+name);
        try {
            PrintWriter out = response.getWriter();
            out.println("<html><body><h1>"+System.currentTimeMillis()+"</h1> </body> </html>");
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
