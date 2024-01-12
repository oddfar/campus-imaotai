package com.oddfar.campus.framework.mapper;

import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysOperLogEntity;

import java.util.Arrays;
import java.util.List;

/**
 * 操作日志 数据层
 */
public interface SysOperLogMapper extends BaseMapperX<SysOperLogEntity> {


    /**
     * 分页查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    default PageResult<SysOperLogEntity> selectOperLogPage(SysOperLogEntity operLog) {

        return selectPage(new LambdaQueryWrapperX<SysOperLogEntity>()
                .likeIfPresent(SysOperLogEntity::getAppName, operLog.getAppName())
                .likeIfPresent(SysOperLogEntity::getLogName, operLog.getLogName())
                .eqIfPresent(SysOperLogEntity::getStatus,operLog.getStatus())
                .eqIfPresent(SysOperLogEntity::getOperIp, operLog.getOperIp())
                .eqIfPresent(SysOperLogEntity::getOperId,operLog.getOperId())
                .betweenIfPresent(SysOperLogEntity::getOperTime, operLog.getParams())
                .orderByDesc(SysOperLogEntity::getOperId)
        );
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    default int deleteOperLogByIds(Long[] operIds) {

        return deleteBatchIds(Arrays.asList(operIds));
    }


    /**
     * 清空操作日志
     */
    public void cleanOperLog();

    /**
     * 查询系统操作日志集合
     * @return
     */
    default List<SysOperLogEntity> selectOperLogList(SysOperLogEntity operLog){
        return selectList(new LambdaQueryWrapperX<SysOperLogEntity>()
                .likeIfPresent(SysOperLogEntity::getAppName, operLog.getAppName())
                .likeIfPresent(SysOperLogEntity::getLogName, operLog.getLogName())
                .eqIfPresent(SysOperLogEntity::getOperIp, operLog.getOperIp())
                .betweenIfPresent(SysOperLogEntity::getOperTime, operLog.getParams())
        );
    }
}
