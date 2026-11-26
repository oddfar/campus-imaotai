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
        if (operLog.getStatus() == 0) {
            //预约成功
            title = iUser.getRemark() + "-i茅台执行成功";
            content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
            AsyncManager.me().execute(sendNotice(token, title, content, "txt"));
        } else {
            //预约失败
            title = iUser.getRemark() + "-i茅台执行失败";
            content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
            AsyncManager.me().execute(sendNotice(token, title, content, "txt"));
        }


    }

    /**
     * 构建推送标题 (#333)
     *
     * @param remark    用户备注
     * @param eventType 事件类型 {@link com.oddfar.campus.business.service.IMTLogFactory}
     * @param status    0成功 1失败
     * @return 标题：{备注}-i茅台-{类型}-{成功|失败}，类型不识别时退化为 {备注}-i茅台{成功|失败}
     */
    public static String buildTitle(String remark, int eventType, int status) {
        String typeName;
        switch (eventType) {
            case 1:
                typeName = "-预约-";
                break;
            case 2:
                typeName = "-耐力值-";
                break;
            case 3:
                typeName = "-旅行-";
                break;
            case 4:
                typeName = "-申购-";
                break;
            default:
                typeName = "";
                break;
        }
        return remark + "-i茅台" + typeName + (status == 0 ? "成功" : "失败");
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
