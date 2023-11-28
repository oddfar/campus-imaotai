package com.oddfar.campus.business.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oddfar.campus.common.domain.BaseEntity;
import com.oddfar.campus.common.utils.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

/**
 * I茅台用户对象 i_user
 *
 * @author oddfar
 * @date 2023-07-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("i_user")
public class IUser extends BaseEntity {
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
     * cookie
     */
    private String cookie;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 商品预约code，用@间隔
     */
    private String itemCode;

    /**
     * 门店商品ID
     */
    private String ishopId;

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
     * 预约的分钟（1-59）
     */
    private int minute;

    /**
     * 随机分钟预约，9点取一个时间（0:随机，1:不随机）
     */
    private String randomMinute;

    /**
     * 类型(1：预约本市出货量最大的门店，2：预约你的位置附近门店)
     */
    private int shopType;

    /**
     * push_plus_token
     */
    private String pushPlusToken;

    /**
     * 返回参数
     */
    @JsonIgnore
    private String jsonResult;

    /**
     * 备注
     */
    private String remark;

    /**
     * token过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    @TableField(exist = false)
    private Map<String, Object> params;

    public IUser() {

    }

    public IUser(Long mobile, JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        this.userId = data.getLong("userId");
        this.mobile = mobile;
        this.token = data.getString("token");
        this.cookie = data.getString("cookie");
        this.jsonResult = StringUtils.substring(jsonObject.toJSONString(), 0, 2000);

//        if (StringUtils.isEmpty(this.remark)) {
//            this.remark = data.getString("userName");
//        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date thirtyDaysLater = calendar.getTime();
        this.expireTime = thirtyDaysLater;
    }

    public IUser(Long mobile, String deviceId, JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        this.userId = data.getLong("userId");
        this.mobile = mobile;
        this.token = data.getString("token");
        this.cookie = data.getString("cookie");
        this.deviceId = deviceId.toLowerCase();
        this.jsonResult = StringUtils.substring(jsonObject.toJSONString(), 0, 2000);

        if (StringUtils.isEmpty(this.remark)) {
            this.remark = data.getString("userName");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date thirtyDaysLater = calendar.getTime();
        this.expireTime = thirtyDaysLater;
    }

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public int getMinute() {
        if (minute > 59 || minute < 1) {
            this.minute = 5;
        }
        return minute;
    }

}
