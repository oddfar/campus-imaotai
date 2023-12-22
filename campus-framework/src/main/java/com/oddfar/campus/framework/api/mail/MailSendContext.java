package com.oddfar.campus.framework.api.mail;

import cn.hutool.extra.spring.SpringUtil;

/**
 * 邮件发送的api上下文
 *
 * @author oddfar
 */
public class MailSendContext {

    /**
     * 获取邮件发送的接口
     */
    public static MailSendApi me() {
        return SpringUtil.getBean(MailSendApi.class);
    }


}
