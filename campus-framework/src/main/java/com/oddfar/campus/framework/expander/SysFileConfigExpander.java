package com.oddfar.campus.framework.expander;

import com.oddfar.campus.framework.api.sysconfig.ConfigContext;
import org.springframework.stereotype.Component;

/**
 * 系统文件配置读取
 */
@Component
public class SysFileConfigExpander {
    /**
     * 获取文件保存目录
     */
    public static String getProfile() {

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
     * 获取导入上传路径
     */
    public static String getImportPath()
    {
        return getProfile() + "/import";
    }
    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }

}
