package com.oddfar.campus.business.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * i茅台用户请求对象
 */
@Data
public class IUserRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
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
     * 完整地址
     */
    private String address;

    /**
     * 预约的分钟（1-59）
     */
    private int minute;

    /**
     * 随机分钟预约，9点取一个时间（0:随机，1:不随机）
     */
    private String randomMinute;

    /**
     * 类型
     */
    private int shopType;

    /**
     * 门店商品ID
     */
    private String ishopId;

    /**
     * push_plus_token
     */
    private String pushPlusToken;

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
