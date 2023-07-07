package com.oddfar.campus.common.annotation;

import com.oddfar.campus.common.enums.ResBizTypeEnum;

import java.lang.annotation.*;

/**
 * 资源标识
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiResource {

    /**
     * 资源编码唯一标识
     * 可不填写此注解属性，默认生成的编码标识为: 控制器类名称 + 分隔符 + 方法名称
     */
    String code() default "";

    /**
     * 应用编码
     * 用于区分不同业务
     */
    String appCode() default "";

    /**
     * 资源名称(必填项)
     */
    String name() default "";

    /**
     * 资源的类型，系统类还是业务类资源
     */
    ResBizTypeEnum resBizType() default ResBizTypeEnum.BUSINESS;

}
