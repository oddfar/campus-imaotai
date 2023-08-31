package com.oddfar.campus.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oddfar.campus.business.domain.IMTItemInfo;
import com.oddfar.campus.business.entity.IShop;

import java.util.List;

public interface IShopService extends IService<IShop> {

    List<IShop> selectShopList();

    /**
     * 刷新数据库i茅台shop列表
     */
    void refreshShop();

    /**
     * 获取当天的sessionId
     */
    String getCurrentSessionId();

    /**
     * 刷新i茅台预约商品列表
     */
    void refreshItem();

    IShop selectByIShopId(String iShopId);

    /**
     * 查询所在省市的投放产品和数量
     *
     * @param province 省份，例如：河北省，北京市
     * @param itemId   项目id即预约项目code
     */
    List<IMTItemInfo> getShopsByProvince(String province, String itemId);

    /**
     * @param shopType 1：预约本市出货量最大的门店，2：预约你的位置附近门店
     * @param itemId   项目id即预约项目code
     * @param province 省份，例如：河北省，北京市
     * @param city     市：例如石家庄市
     * @return
     */
    String getShopId(int shopType, String itemId, String province, String city, String lat, String lng);


}
