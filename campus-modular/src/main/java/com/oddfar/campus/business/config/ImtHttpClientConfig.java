package com.oddfar.campus.business.config;

import com.oddfar.campus.imt.http.domain.ImtHttpClient;
import com.oddfar.campus.imt.http.rest.ImtHttpClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * i茅台Http client引入
 *
 * @author oddfar
 * @date 2024/7/20
 */
@Configuration
public class ImtHttpClientConfig {

    @Bean
    public ImtHttpClient imtHttpClient() {
        ImtHttpClient imtHttpClient = new ImtHttpClientImpl();
        return imtHttpClient;
    }
}
