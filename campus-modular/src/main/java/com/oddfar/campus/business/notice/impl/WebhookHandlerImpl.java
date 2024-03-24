package com.oddfar.campus.business.notice.impl;

import cn.hutool.http.HttpUtil;
import com.oddfar.campus.business.notice.AbstractNoticeHandler;
import com.oddfar.campus.business.notice.NoticeConfig;
import com.oddfar.campus.common.enums.NoticeEnum;
import com.oddfar.campus.common.utils.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gitsilence
 * @date 2024-03-17
 */
@Component
public class WebhookHandlerImpl extends AbstractNoticeHandler {

    @Autowired
    private NoticeConfig noticeConfig;

    @Override
    public NoticeEnum getBizType() {
        return NoticeEnum.WEB_HOOk;
    }

    @Override
    public TimerTask sendNotice(String token, String title, String content, String template) {
        return new TimerTask() {
            @Override
            public void run() {
                String apiUrl = StringUtils.replace(noticeConfig.getApiUrl(), "{token}", token);
                String requestBody = null;
                try {
                    requestBody = StringUtils.replace(noticeConfig.getRequestBody(), "{title}", title)
                        .replace("{content}", URLEncoder.encode(content, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                HttpUtil.post(apiUrl, requestBody);
            }
        };
    }
}
