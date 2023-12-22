package com.oddfar.campus.framework.api.mail;

import java.util.List;

/**
 * 发送邮件Api
 * （后期整合下阿里云 腾讯云）
 *
 * @author oddfar
 */
public interface MailSendApi {

    /**
     * 以发送QQ邮箱为例子
     *
     * @param tos     对方的邮箱地址，可以是单个，也可以是多个（Collection表示）
     * @param subject 标题
     * @param content 邮件正文，可以是文本，也可以是HTML内容
     * @param isHtml  是否为HTML，如果是，那参数content识别为HTML内容
     */
    void sendQQMail(List<String> tos, String subject, String content, Boolean isHtml);

}
