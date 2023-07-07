package com.oddfar.campus.framework.service;

import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysLoginLogEntity;

import java.util.List;

/**
 * 系统访问日志情况信息 服务层
 */
public interface SysLoginLogService {
    /**
     * 查询系统登录日志分页数据
     *
     * @param logininfor 访问日志对象
     * @return 登录记录分页数据
     */
    public PageResult<SysLoginLogEntity> selectLogininforPage(SysLoginLogEntity logininfor);

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLoginLogEntity logininfor);

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    public List<SysLoginLogEntity> selectLogininforList(SysLoginLogEntity logininfor);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    public int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     */
    public void cleanLogininfor();
}
