package com.oddfar.campus.framework.api.resource;


import com.oddfar.campus.framework.api.resource.impl.DefaultResourceCollector;
import com.oddfar.campus.framework.listener.ApiResourceScanner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 资源的自动配置
 */
@Configuration
public class ApiResourceAutoConfig {

    /**
     * 资源搜集器
     */
    @Bean
    @ConditionalOnMissingBean(ApiResourceScanner.class)
    public ApiResourceScanner apiResourceScanner(ResourceCollectorApi resourceCollectorApi) {
        return new ApiResourceScanner(resourceCollectorApi);
    }


    /**
     * 资源搜集api
     */
    @Bean
    @ConditionalOnMissingBean(ResourceCollectorApi.class)
    public ResourceCollectorApi resourceCollectorApi() {
        return new DefaultResourceCollector();
    }

}
