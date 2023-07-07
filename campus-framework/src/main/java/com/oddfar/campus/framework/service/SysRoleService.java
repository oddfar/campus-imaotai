package com.oddfar.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysRoleEntity;
import com.oddfar.campus.common.domain.entity.SysUserRoleEntity;

import java.util.List;
import java.util.Set;

public interface SysRoleService extends IService<SysUserRoleEntity> {


    PageResult<SysRoleEntity> page(SysRoleEntity sysRoleEntity);

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    List<SysRoleEntity> selectRoleList(SysRoleEntity role);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectRolePermissionByUserId(Long userId);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    SysRoleEntity selectRoleById(Long roleId);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<SysRoleEntity> selectRoleAll();

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRoleEntity> selectRolesByUserId(Long userId);


    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
     int insertRole(SysRoleEntity role);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
     int updateRole(SysRoleEntity role);

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
     int updateRoleStatus(SysRoleEntity role);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
     int deleteRoleByIds(Long[] roleIds);

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    int deleteAuthUser(SysUserRoleEntity userRole);

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    int deleteAuthUsers(Long roleId, Long[] userIds);

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    boolean insertAuthUsers(Long roleId, Long[] userIds);

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
     int countUserRoleByRoleId(Long roleId);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
     boolean checkRoleNameUnique(SysRoleEntity role);

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean checkRoleKeyUnique(SysRoleEntity role);

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    void checkRoleAllowed(SysRoleEntity role);

    /**
     * 重置角色的资源和菜单权限缓存
     */
    void resetRoleAuthCache();


}
