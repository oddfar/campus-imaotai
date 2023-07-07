package com.oddfar.campus.business.mapper;

import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;

import java.util.Date;
import java.util.List;

/**
 * I茅台用户Mapper接口
 *
 * @author oddfar
 * @date 2023-07-02
 */

public interface IUserMapper extends BaseMapperX<IUser> {
    default PageResult<IUser> selectPage(IUser iUser) {

        return selectPage(new LambdaQueryWrapperX<IUser>()
                        .eqIfPresent(IUser::getUserId, iUser.getUserId())
                        .eqIfPresent(IUser::getMobile, iUser.getMobile())
                        .eqIfPresent(IUser::getProvinceName, iUser.getProvinceName())
//                .betweenIfPresent(IUser::getCreateTime, iUser.getParams())
        );

    }

    default List<IUser> selectReservationUser() {
        return selectList(new LambdaQueryWrapperX<IUser>()
                .gt(IUser::getExpireTime, new Date())
                .ne(IUser::getLat,"")
                .ne(IUser::getLng,"")
                .ne(IUser::getShopType,"")
                .ne(IUser::getItemCode,"")

        );

    }

}
