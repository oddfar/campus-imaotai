package com.oddfar.campus.business.api;

import cn.hutool.http.HttpUtil;
import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.manager.AsyncManager;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * @author zhiyuan
 */
public class PushPlusApi {


    public static void sendNotice(IUser iUser, ILog operLog) {
        String token = iUser.getPushPlusToken();
        if (StringUtils.isEmpty(token)) {
            return;
        }
        String title, content;
        String type = classifyOperType(operLog.getLogContent());
        if (operLog.getStatus() == 0) {
            //执行成功
            title = iUser.getRemark() + "-i茅台" + type + "成功";
            content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
            AsyncManager.me().execute(sendNotice(token, title, content, "txt"));
        } else {
            //执行失败
            title = iUser.getRemark() + "-i茅台" + type + "失败";
            content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
            AsyncManager.me().execute(sendNotice(token, title, content, "txt"));
        }

    }

    /**
     * 根据日志内容识别本次操作的类型，用于推送标题分类（预约/旅行/申购耐力值）
     *
     * @param logContent 日志内容
     * @return 操作类型名称
     */
    private static String classifyOperType(String logContent) {
        if (StringUtils.isEmpty(logContent)) {
            return "执行";
        }
        // 预约日志的“结果返回”中 successDesc 含“申购”字样，故“预约”需优先于“申购”判断
        if (logContent.contains("旅行")) {
            return "旅行";
        }
        if (logContent.contains("预约")) {
            return "预约";
        }
        if (logContent.contains("申购耐力值")) {
            return "申购耐力值";
        }
        if (logContent.contains("申购")) {
            return "申购";
        }
        return "执行";
    }

    /**
     * push推送
     *
     * @param token    token
     * @param title    消息标题
     * @param content  具体消息内容
     * @param template 发送消息模板
     */
    public static TimerTask sendNotice(String token, String title, String content, String template) {
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

}
