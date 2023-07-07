package com.oddfar.campus.framework.expander;

import com.oddfar.campus.framework.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 系统配置读取
 */
@Component
public class SysConfigExpander {

    private static SysConfigService configService;

    @Autowired
    private SysConfigService sysConfigService;

    @PostConstruct
    public void init() {
        configService = sysConfigService;
    }


    /**
     * 用户默认头像url
     */
    public static String getUserDefaultAvatar() {
        return configService.selectConfigByKey("sys.user.default.avatar");
    }

    /**
     * 验证码类型
     *
     * @return
     */
    public static String getLoginCaptchaType() {
        return configService.selectConfigByKey("sys.login.captchaType", String.class, "math");
    }


    /**
     * 获取文件保存目录
     *
     * @return
     */
    public static String getFileProfile() {

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return configService.selectConfigByKey("sys.local.profile.win", String.class, "D:\\uploadPath");
        }
        if (osName.contains("mac")) {
            return configService.selectConfigByKey("sys.local.profile.mac", String.class, "~/uploadPath");
        }
        if (osName.contains("linux")) {
            return configService.selectConfigByKey("sys.local.profile.linux", String.class, "/data/uploadPath");
        }
        return null;
    }

}
