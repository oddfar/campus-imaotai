package com.oddfar.campus.framework.api.sysconfig;

import cn.hutool.extra.spring.SpringUtil;
import com.oddfar.campus.framework.service.SysConfigService;

/**
 * 系统配置
 */
public class ConfigContext {

    /**
     * 获取系统配置操作接口
     */
    public static SysConfigService me() {
        return SpringUtil.getBean(SysConfigService.class);
    }

}
