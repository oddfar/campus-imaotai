package com.oddfar.campus.framework.service;

import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysOperLogEntity;

import java.util.List;

/**
 * 操作日志 服务层
 */
public interface SysOperLogService {
    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    public void insertOperlog(SysOperLogEntity operLog);

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    PageResult<SysOperLogEntity> selectOperLogPage(SysOperLogEntity operLog);

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public int deleteOperLogByIds(Long[] operIds);

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    public SysOperLogEntity selectOperLogById(Long operId);

    /**
     * 清空操作日志
     */
    public void cleanOperLog();

    /**
     * 查询操作日志列表
     *
     * @param operLog 操作日志对象
     * @return
     */
    List<SysOperLogEntity> selectOperLogList(SysOperLogEntity operLog);
}
