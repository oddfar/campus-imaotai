package com.oddfar.campus.business.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.oddfar.campus.common.utils.StringUtils;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;

/**
 * I茅台用户对象 i_user
 *
 * @author oddfar
 * @date 2023-07-02
 */
@Data
@TableName("i_user")
public class IUser {
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @TableId
    private Long mobile;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * token
     */
    private String token;

    /**
     * 商品预约code，用@间隔
     */
    private String itemCode;

    /**
     * 省份
     */
    private String provinceName;

    /**
     * 城市
     */
    private String cityName;

    /**
     * 完整地址
     */
    private String address;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 经度
     */
    private String lng;

    /**
     * 类型
     */
    private int shopType;

    /**
     * 返回参数
     */
    private String jsonResult;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * token过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    public IUser() {
    }

    public IUser(Long mobile, JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        this.userId = data.getLong("userId");
        this.mobile = mobile;
        this.token = data.getString("token");
        this.jsonResult = StringUtils.substring(jsonObject.toJSONString(), 0, 2000);

        this.createTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date thirtyDaysLater = calendar.getTime();
        this.expireTime = thirtyDaysLater;
    }
}
