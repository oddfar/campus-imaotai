package com.oddfar.campus.business.mapper;

import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;
import org.apache.ibatis.annotations.Select;

/**
 * I茅台日志Mapper接口
 *
 * @author oddfar
 * @date 2023-08-01
 */
public interface ILogMapper extends BaseMapperX<ILog> {

    default PageResult<ILog> selectPage(ILog iLog) {

        return selectPage(new LambdaQueryWrapperX<ILog>()
                .eqIfPresent(ILog::getMobile, iLog.getMobile())
                .eqIfPresent(ILog::getStatus, iLog.getStatus())
                .betweenIfPresent(ILog::getOperTime, iLog.getParams())
                .orderByDesc(ILog::getLogId)
        );

    }

    default PageResult<ILog> selectPage(ILog iLog, Long userId) {

        return selectPage(new LambdaQueryWrapperX<ILog>()
                .eqIfPresent(ILog::getMobile, iLog.getMobile())
                .eqIfPresent(ILog::getStatus, iLog.getStatus())
                .eq(ILog::getCreateUser, userId)
                .betweenIfPresent(ILog::getOperTime, iLog.getParams())
                .orderByDesc(ILog::getLogId)
        );

    }

    @Select("truncate table i_log")
    void cleanLog();

}
