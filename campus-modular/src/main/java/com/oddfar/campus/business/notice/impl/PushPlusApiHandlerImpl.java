package com.oddfar.campus.business.notice.impl;

import cn.hutool.http.HttpUtil;
import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.business.notice.AbstractNoticeHandler;
import com.oddfar.campus.common.enums.NoticeEnum;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.manager.AsyncManager;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import org.springframework.stereotype.Component;

/**
 * @author zhiyuan
 */
@Component
public class PushPlusApiHandlerImpl extends AbstractNoticeHandler {


    /**
     * push推送
     *
     * @param token    token
     * @param title    消息标题
     * @param content  具体消息内容
     * @param template 发送消息模板
     */
    public TimerTask sendNotice(String token, String title, String content, String template) {
        return new TimerTask() {
            @Override
            public void run() {
                String url = "http://www.pushplus.plus/send";
                Map<String, Object> map = new HashMap<>();
                map.put("token", token);
                map.put("title", title);
                map.put("content", content);
                if (StringUtils.isEmpty(template)) {
                    map.put("template", "html");
                }
                HttpUtil.post(url, map);
            }
        };
    }

    @Override
    public NoticeEnum getBizType() {
        return NoticeEnum.PUSH_PLUS;
    }
}
