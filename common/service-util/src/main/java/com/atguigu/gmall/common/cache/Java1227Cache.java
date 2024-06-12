package com.atguigu.gmall.common.cache;


import java.lang.annotation.*;

/**
 * 自定义的一个注解接口
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Java1227Cache {
    //前缀属性
    String prefix() default "cache";
}
