package com.oddfar.campus.business.service;

import com.oddfar.campus.business.entity.IUser;

public interface IMTService {
    /**
     * 获取i茅台app版本号
     *
     * @return
     */
    String getMTVersion();

    /**
     * 刷新i茅台app版本号
     */
    void refreshMTVersion();

    /**
     * 发送手机验证码
     *
     * @param mobile 手机号
     */
    boolean sendCode(String mobile);

    /**
     * 登录i茅台
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return
     */
    boolean login(String mobile, String code);

    /**
     * 预约
     */
    void reservation(IUser iUser);

    /**
     * 批量预约
     */
    void reservationBatch();
}
