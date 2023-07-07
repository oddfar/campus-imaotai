package com.oddfar.campus.common.domain.model;

import lombok.Data;

/**
 * 登录用户身份信息
 *
 */
@Data
public class LoginUserToken {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    public LoginUserToken() {
    }

    public LoginUserToken(LoginUser loginUser) {
        this.userId = loginUser.getUserId();
        this.token = loginUser.getToken();
    }
}
