package com.oddfar.campus.business.mapper;

import com.oddfar.campus.business.entity.IShop;
import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;
import org.apache.ibatis.annotations.Update;

/**
 * I茅台商品Mapper接口
 *
 * @author oddfar
 * @date 2023-07-02
 */

public interface IShopMapper extends BaseMapperX<IShop> {
    //清空指定表
    @Update("truncate table i_shop")
    void truncateShop();

    default PageResult<IShop> selectPage(IShop iShop) {

        return selectPage(new LambdaQueryWrapperX<IShop>()
                .eqIfPresent(IShop::getIShopId,iShop.getIShopId())
                .likeIfPresent(IShop::getProvinceName, iShop.getProvinceName())
                .likeIfPresent(IShop::getDistrictName, iShop.getDistrictName())
                .likeIfPresent(IShop::getCityName, iShop.getCityName())
                .likeIfPresent(IShop::getTenantName, iShop.getTenantName())
                .betweenIfPresent(IShop::getCreateTime, iShop.getParams())
        );

    }
}
