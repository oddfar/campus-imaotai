package com.oddfar.campus.business.service;

import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.common.domain.PageResult;

/**
 * i茅台 日志
 */
public interface IMTLogService {

    PageResult<ILog> page(ILog iLog);

    /**
     * 新增操作日志
     *
     * @param iLog 操作日志对象
     */
    public int insertLog(ILog iLog);

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public int deleteLogByIds(Long[] operIds);

    /**
     * 清空操作日志
     */
    public void cleanLog();

}
