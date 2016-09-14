package com.mvc.annotations;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2016/9/13.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
