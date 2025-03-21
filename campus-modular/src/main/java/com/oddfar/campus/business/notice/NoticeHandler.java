package com.oddfar.campus.business.notice;

import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.business.entity.IUser;
import java.util.TimerTask;

/**
 * @author gitsilence
 * @date 2024-03-17
 */
public interface NoticeHandler {

    /**
     * 发送通知
     * @param token
     * @param title
     * @param content
     * @param template
     */
    TimerTask sendNotice(String token, String title, String content, String template);

}
