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
     * @param mobile   手机号
     * @param deviceId 设备id
     */
    Boolean sendCode(String mobile, String deviceId);

    /**
     * 登录i茅台
     *
     * @param mobile   手机号
     * @param code     验证码
     * @param deviceId 设备id
     * @return
     */
    boolean login(String mobile, String code, String deviceId);

    /**
     * 预约
     */
    void reservation(IUser iUser);

    /**
     * 获取申购耐力值
     */
    String getEnergyAward(IUser iUser);

    /**
     * 获得旅行奖励
     *
     * @param iUser
     * @return
     */
    void getTravelReward(IUser iUser);
//
//    /**
//     * 获取累计申购奖励
//     * @param iUser
//     */
//    void getCumulatively(IUser iUser);

    /**
     * 批量预约
     */
    void reservationBatch();

    /**
     * 批量获得旅行奖励
     */
    void getTravelRewardBatch();

    /**
     * 刷新版本号，预约item，门店shop列表，
     */
    void refreshAll();

    /**
     * 每日预约申购结果
     */
    void appointmentResults();


}
