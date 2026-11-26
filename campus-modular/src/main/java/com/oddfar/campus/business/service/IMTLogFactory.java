package com.oddfar.campus.business.service;

import com.oddfar.campus.business.api.DingTalkApi;
import com.oddfar.campus.business.api.PushPlusApi;
import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.common.utils.SpringUtils;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.api.mail.MailSendApi;
import com.oddfar.campus.framework.manager.AsyncManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.TimerTask;

/**
 * i茅台日志记录
 */
public class IMTLogFactory {

    private static final Logger logger = LoggerFactory.getLogger(IMTLogFactory.class);

    /**
     * 事件类型：预约
     */
    public static final int TYPE_RESERVE = 1;
    /**
     * 事件类型：申购耐力值
     */
    public static final int TYPE_ENERGY = 2;
    /**
     * 事件类型：旅行奖励
     */
    public static final int TYPE_TRAVEL = 3;
    /**
     * 事件类型：申购结果
     */
    public static final int TYPE_APPOINTMENT = 4;


    public static void reservation(IUser iUser, int eventType, String logContent) {
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
        //推送，按账号配置的渠道逐个发送，单渠道失败不影响其他渠道
        String title = PushPlusApi.buildTitle(iUser.getRemark(), eventType, operLog.getStatus());
        String content = iUser.getMobile() + System.lineSeparator() + logContent;
        try {
            PushPlusApi.sendNotice(iUser, operLog, eventType);
        } catch (Exception e) {
            logger.error("pushplus推送失败:{}", e.getMessage(), e);
        }
        try {
            DingTalkApi.sendNotice(iUser, operLog, eventType);
        } catch (Exception e) {
            logger.error("钉钉推送失败:{}", e.getMessage(), e);
        }
        try {
            if (!StringUtils.isEmpty(iUser.getMailAddr())) {
                SpringUtils.getBean(MailSendApi.class).sendQQMail(Collections.singletonList(iUser.getMailAddr()), title, content, false);
            }
        } catch (Exception e) {
            logger.error("邮件推送失败:{}", e.getMessage(), e);
        }
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
