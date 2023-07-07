package com.oddfar.campus.common.annotation;

import java.lang.annotation.*;

/**
 * 用来标记在控制器类或方法上，进行判断是否需要对接口进行日志记录
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 是否进行日志记录，默认开启
     */
    boolean openLog() default true;

}
