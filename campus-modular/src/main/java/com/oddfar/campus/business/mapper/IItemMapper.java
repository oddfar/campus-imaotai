package com.oddfar.campus.business.mapper;

import com.oddfar.campus.business.entity.IItem;
import com.oddfar.campus.common.core.BaseMapperX;
import org.apache.ibatis.annotations.Update;

/**
 * I茅台预约商品Mapper接口
 *
 * @author oddfar
 * @date 2023-07-02
 */
public interface IItemMapper extends BaseMapperX<IItem> {
    //清空指定表
    @Update("truncate table i_item")
    void truncateItem();
}
