package com.oddfar.campus.business.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * I茅台商品对象 i_shop
 *
 * @author oddfar
 * @date 2023-07-02
 */
@Data
@TableName("i_shop")
public class IShop {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId
    private Long shopId;

    /**
     * 商品ID
     */
    private String iShopId;

    /**
     * 省份
     */
    private String provinceName;

    /**
     * 城市
     */
    private String cityName;

    /**
     * 地区
     */
    private String districtName;

    /**
     * 完整地址
     */
    private String fullAddress;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 经度
     */
    private String lng;

    /**
     * 名称
     */
    private String name;

    /**
     * 公司名称
     */
    private String tenantName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(exist = false)
    private Map<String, Object> params;

    /**
     * 距离
     */
    @TableField(exist = false)
    private Double distance;


    public IShop() {
    }

    public IShop(String iShopId, JSONObject jsonObject) {
        this.iShopId = iShopId;
        this.provinceName = jsonObject.getString("provinceName");
        this.cityName = jsonObject.getString("cityName");
        this.districtName = jsonObject.getString("districtName");
        this.fullAddress = jsonObject.getString("fullAddress");
        this.lat = jsonObject.getString("lat");
        this.lng = jsonObject.getString("lng");
        this.name = jsonObject.getString("name");
        this.tenantName = jsonObject.getString("tenantName");
        this.createTime = new Date();

    }

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

}
