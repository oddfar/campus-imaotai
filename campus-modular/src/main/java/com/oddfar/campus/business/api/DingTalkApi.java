package com.oddfar.campus.business.api;

import cn.hutool.http.HttpUtil;
import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.manager.AsyncManager;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * 钉钉群机器人推送
 */
public class DingTalkApi {

    /**
     * 发送钉钉通知，webhook为空则跳过
     */
    public static void sendNotice(IUser iUser, ILog operLog, int eventType) {
        String webhook = iUser.getDingWebhook();
        if (StringUtils.isEmpty(webhook)) {
            return;
        }
        String title = PushPlusApi.buildTitle(iUser.getRemark(), eventType, operLog.getStatus());
        String content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
        AsyncManager.me().execute(sendNotice(webhook, iUser.getDingSecret(), title, content));
    }

    /**
     * 加签：HmacSHA256(key=secret, msg=timestamp+"\n"+secret) → Base64 → urlencode
     *
     * @param timestamp 毫秒时间戳
     * @param secret    加签密钥
     */
    public static String sign(long timestamp, String secret) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return URLEncoder.encode(Base64.getEncoder().encodeToString(signData), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("钉钉加签失败", e);
        }
    }

    /**
     * push推送
     *
     * @param webhook 钉钉机器人webhook
     * @param secret  加签密钥，为空则不加签
     * @param title   消息标题
     * @param content 具体消息内容
     */
    public static TimerTask sendNotice(String webhook, String secret, String title, String content) {
        return new TimerTask() {
            @Override
            public void run() {
                String url = webhook;
                if (!StringUtils.isEmpty(secret)) {
                    long timestamp = System.currentTimeMillis();
                    url += "&timestamp=" + timestamp + "&sign=" + sign(timestamp, secret);
                }
                Map<String, Object> markdown = new HashMap<>();
                markdown.put("title", title);
                markdown.put("text", content);
                Map<String, Object> map = new HashMap<>();
                map.put("msgtype", "markdown");
                map.put("markdown", markdown);
                HttpUtil.post(url, map);
            }
        };
    }

}
