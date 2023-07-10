package com.oddfar.campus.business.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.common.domain.entity.SysOperLogEntity;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.manager.AsyncFactory;
import com.oddfar.campus.framework.manager.AsyncManager;

import java.util.Date;

/**
 * i茅台日志记录
 */
public class IMTLogFactory {


    public static void reservation(IUser iUser, String shopId, JSONObject json) {
        //{"code":2000,"data":{"successDesc":"申购完成，请于7月6日18:00查看预约申购结果","reservationList":[{"reservationId":17053404357,"sessionId":678,"shopId":"233331084001","reservationTime":1688608601720,"itemId":"10214","count":1}],"reservationDetail":{"desc":"申购成功后将以短信形式通知您，请您在申购成功次日18:00前确认支付方式，并在7天内完成提货。","lotteryTime":1688637600000,"cacheValidTime":1688637600000}}}
        SysOperLogEntity operLog = new SysOperLogEntity();

        operLog.setOperTime(new Date());
        operLog.setAppName("i茅台预约");

        if (json.getString("code").equals("2000")) {
            operLog.setLogName("预约成功");
        } else {
            operLog.setLogName("预约失败");
            operLog.setStatus(1);
        }
        operLog.setOperUserId(iUser.getMobile());
        String str = String.format("[userId]:%s\n[shopId]:%s", iUser.getUserId().toString(), shopId);
        operLog.setLogContent(str);

        operLog.setJsonResult(StringUtils.substring(JSON.toJSONString(json), 0, 2000));

        AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
    }

    public static void reservation(IUser iUser, String shopId) {
        SysOperLogEntity operLog = new SysOperLogEntity();

        operLog.setOperTime(new Date());
        operLog.setAppName("i茅台预约");

        operLog.setLogName("预约失败");
        operLog.setOperUserId(iUser.getMobile());
        String str = String.format("[userId]:%s\n[shopId]:%s\nshopId为空", iUser.getUserId().toString(), shopId);
        operLog.setLogContent(str);
        operLog.setStatus(1);

        AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
    }

    public static void reservation(IUser iUser, Exception e) {
        SysOperLogEntity operLog = new SysOperLogEntity();

        operLog.setOperTime(new Date());
        operLog.setAppName("i茅台预约");

        operLog.setLogName("预约失败");
        operLog.setOperUserId(iUser.getMobile());
        String str = String.format("执行报错");
        operLog.setLogContent(str);
        operLog.setStatus(1);
        operLog.setErrorMsg(e.getMessage());

        AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
    }

}
