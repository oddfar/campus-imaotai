package com.oddfar.campus.business.notice;

import com.oddfar.campus.common.enums.NoticeEnum;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author gitsilence
 * @date 2024-03-17
 */
public abstract class AbstractNoticeHandler implements NoticeHandler, InitializingBean {


    public abstract NoticeEnum getBizType();

    @Override
    public void afterPropertiesSet() throws Exception {
        NoticeFactory.addService(getBizType(), this);
    }
}
