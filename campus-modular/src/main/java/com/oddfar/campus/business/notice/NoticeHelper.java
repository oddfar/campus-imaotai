package com.oddfar.campus.business.notice;

import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.common.enums.NoticeEnum;
import com.oddfar.campus.common.utils.SpringUtils;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.manager.AsyncManager;

/**
 * @author gitsilence
 * @date 2024-03-17
 */
public class NoticeHelper {

    public static void sendNotice(IUser iUser, ILog operLog) {
        String token = iUser.getPushPlusToken();
        String title, content;
        if (operLog.getStatus() == 0) {
            //预约成功
            title = iUser.getRemark() + "-i茅台执行成功";
            content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
        } else {
            //预约失败
            title = iUser.getRemark() + "-i茅台执行失败";
            content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
        }
        AsyncManager.me().execute(NoticeFactory.getService(NoticeEnum.getNoticeType(SpringUtils.getBean(NoticeConfig.class)
                .getNoticeType()))
            .sendNotice(token, title, content, "txt"));
    }

}
