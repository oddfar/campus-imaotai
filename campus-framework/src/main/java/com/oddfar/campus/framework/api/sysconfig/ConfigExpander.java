package com.oddfar.campus.framework.api.sysconfig;

import cn.hutool.core.convert.Convert;

public class ConfigExpander {
    /**
     * 用户默认头像url
     */
    public static String getUserDefaultAvatar() {
        return ConfigContext.me().selectConfigByKey("sys.user.default.avatar");
    }

    /**
     * 验证码类型
     */
    public static String getLoginCaptchaType() {
        return ConfigContext.me().selectConfigByKey("sys.login.captchaType", String.class, "math");
    }

    /**
     * 获取文件保存目录
     */
    public static String getFileProfile() {

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return ConfigContext.me().selectConfigByKey("sys.local.profile.win", String.class, "D:\\uploadPath");
        }
        if (osName.contains("mac")) {
            return ConfigContext.me().selectConfigByKey("sys.local.profile.mac", String.class, "~/uploadPath");
        }
        if (osName.contains("linux")) {
            return ConfigContext.me().selectConfigByKey("sys.local.profile.linux", String.class, "/data/uploadPath");
        }
        return null;
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getFileProfile() + "/avatar";
    }


    /**
     * 全局日志记录，开启则所有请求都将记录日志
     */
    public static Boolean getGlobalControllerOpenFlag() {
        String flag = ConfigContext.me().selectConfigByKey("sys.log.global.flag", String.class, "false");

        return Convert.toBool(flag);

    }

}
