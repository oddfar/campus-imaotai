package com.oddfar.campus.business.notice;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gitsilence
 * @date 2024-03-17
 */
@ConfigurationProperties(prefix = "notice.config")
@Component
@Data
public class NoticeConfig {

    /**
     * 发送类型
     */
    private Integer noticeType = 1;

    /**
     * api url
     */
    private String apiUrl = "";

    /**
     * 请求体
     *
     */
    private String requestBody;

}
