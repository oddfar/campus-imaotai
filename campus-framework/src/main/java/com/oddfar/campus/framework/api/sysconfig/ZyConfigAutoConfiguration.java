package com.oddfar.campus.framework.api.sysconfig;

import com.oddfar.campus.framework.service.SysConfigService;
import com.oddfar.campus.framework.service.impl.SysConfigServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 系统配置自动配置
 */
@Configuration
public class ZyConfigAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SysConfigService.class)
    public SysConfigService configService() {
        return new SysConfigServiceImpl();
    }
}
