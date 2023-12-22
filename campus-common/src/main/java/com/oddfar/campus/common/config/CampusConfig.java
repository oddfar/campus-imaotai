package com.oddfar.campus.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 首页
 *
 * @author oddfar
 */

@Data
@Component
@ConfigurationProperties(prefix = "campus")
public class CampusConfig {

    /**
     * 项目名称
     */
    private String name;

    /**
     * 版本
     */
    private String version;

}
