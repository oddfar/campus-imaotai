package com.oddfar.campus.common.enums;

import lombok.Getter;

/**
 * 接口资源的类别
 */
@Getter
public enum ResBizTypeEnum {

    /**
     * 业务类
     */
    BUSINESS(1, "业务类"),

    /**
     * 系统类
     */
    SYSTEM(2, "系统类");

    private final Integer code;

    private final String message;

    ResBizTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
