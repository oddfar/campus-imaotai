package com.oddfar.campus.common.utils.web;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * 专属于 web 包的工具类
 * 参考 ruoyi-pro 芋道源码
 *
 * @author oddfar
 */
public class WebFrameworkUtils {

    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_ID = "login_user_id";

    public static void setLoginUserId(ServletRequest request, Long userId) {
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID, userId);
    }

    /**
     * 获得当前用户的编号，从请求中
     * 注意：该方法仅限于 framework 框架使用！！！ @SecurityUtils.getUserId()
     *
     * @param request 请求
     * @return 用户编号
     */
    public static Long getLoginUserId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return (Long) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID);
    }
}
