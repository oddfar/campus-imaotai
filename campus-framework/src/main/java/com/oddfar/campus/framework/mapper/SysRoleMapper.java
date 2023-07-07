package com.oddfar.campus.framework.mapper;

import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysRoleMapper extends BaseMapperX<SysRoleEntity> {
    default PageResult<SysRoleEntity> selectPage(SysRoleEntity role) {

        return selectPage(new LambdaQueryWrapperX<SysRoleEntity>()
                .likeIfPresent(SysRoleEntity::getRoleName, role.getRoleName())
                .likeIfPresent(SysRoleEntity::getRoleKey, role.getRoleKey())
                .eqIfPresent(SysRoleEntity::getStatus, role.getStatus())
        );
    }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRoleEntity> selectRolePermissionByUserId(Long userId);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    SysRoleEntity selectRoleById(Long roleId);

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    List<SysRoleEntity> selectRoleList(SysRoleEntity role);

    /**
     * 根据角色权限字符串查询角色数据
     * @param RoleKeys
     * @return
     */
    List<SysRoleEntity> selectRoleListByKey(@Param("RoleKeys") Set<String> RoleKeys);

    /**
     * 校验角色名称是否唯一
     *
     * @param roleName 角色名称
     * @return 角色信息
     */
    SysRoleEntity checkRoleNameUnique(String roleName);

    /**
     * 校验角色权限是否唯一
     *
     * @param roleKey 角色权限
     * @return 角色信息
     */
    SysRoleEntity checkRoleKeyUnique(String roleKey);

    /**
     * 根据用户ID查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    public List<SysRoleEntity> selectRolesByUserName(String userName);

}
