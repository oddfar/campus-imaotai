package com.oddfar.campus.business.service;

import com.oddfar.campus.business.api.PushPlusApi;
import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.common.utils.SpringUtils;
import com.oddfar.campus.framework.manager.AsyncManager;

import java.util.Date;
import java.util.TimerTask;

/**
 * i茅台日志记录
 */
public class IMTLogFactory {


    public static void reservation(IUser iUser, String logContent) {
        //{"code":2000,"data":{"successDesc":"申购完成，请于7月6日18:00查看预约申购结果","reservationList":[{"reservationId":17053404357,"sessionId":678,"shopId":"233331084001","reservationTime":1688608601720,"itemId":"10214","count":1}],"reservationDetail":{"desc":"申购成功后将以短信形式通知您，请您在申购成功次日18:00前确认支付方式，并在7天内完成提货。","lotteryTime":1688637600000,"cacheValidTime":1688637600000}}}
        ILog operLog = new ILog();

        operLog.setOperTime(new Date());

        if (logContent.contains("报错")) {
            //失败
            operLog.setStatus(1);
        } else {
            operLog.setStatus(0);
        }
        operLog.setMobile(iUser.getMobile());
        operLog.setCreateUser(iUser.getCreateUser());
        operLog.setLogContent(logContent);

        AsyncManager.me().execute(recordOper(operLog));
        //推送
        PushPlusApi.sendNotice(iUser, operLog);
    }

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final ILog operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(IMTLogService.class).insertLog(operLog);
            }
        };
    }


}
