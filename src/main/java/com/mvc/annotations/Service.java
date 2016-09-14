package com.mvc.annotations;


import java.lang.annotation.*;

/**
 * Created by Administrator on 2016/9/13.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    String value() default "";
}
