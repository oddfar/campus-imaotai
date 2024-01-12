package com.oddfar.campus.framework.mapper;

import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysLoginLogEntity;

import java.util.List;

/**
 * 系统访问日志情况信息 数据层
 *
 * @author ruoyi
 */
public interface SysLoginLogMapper extends BaseMapperX<SysLoginLogEntity> {

    default PageResult<SysLoginLogEntity> selectLogininforPage(SysLoginLogEntity logininfor) {
        return selectPage(new LambdaQueryWrapperX<SysLoginLogEntity>()
                .eqIfPresent(SysLoginLogEntity::getUserId, logininfor.getUserId())
                .eqIfPresent(SysLoginLogEntity::getUserName,logininfor.getUserName())
                .eqIfPresent(SysLoginLogEntity::getStatus, logininfor.getStatus())
                .betweenIfPresent(SysLoginLogEntity::getLoginTime, logininfor.getParams())
                .orderByDesc(SysLoginLogEntity::getInfoId)
        );
    }


    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    default List<SysLoginLogEntity> selectLogininforList(SysLoginLogEntity logininfor) {
        return selectList(new LambdaQueryWrapperX<SysLoginLogEntity>()
                .eqIfPresent(SysLoginLogEntity::getStatus, logininfor.getStatus())
                .likeIfPresent(SysLoginLogEntity::getUserName, logininfor.getUserName())
                .eqIfPresent(SysLoginLogEntity::getUserId, logininfor.getUserId()));
    }


    /**
     * 清空系统登录日志
     *
     * @return 结果
     */
    int cleanLogininfor();


}
