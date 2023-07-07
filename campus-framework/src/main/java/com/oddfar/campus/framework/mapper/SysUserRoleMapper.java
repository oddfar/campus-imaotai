package com.oddfar.campus.framework.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.domain.entity.SysUserRoleEntity;
import org.apache.ibatis.annotations.Param;

public interface SysUserRoleMapper extends BaseMapperX<SysUserRoleEntity> {

    /**
     * 通过用户ID删除用户和角色关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    default int deleteUserRoleByUserId(Long userId) {
        return delete(new QueryWrapper<SysUserRoleEntity>()
                .eq("user_id" , userId));
    }

    /**
     * 删除用户和角色关联信息
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    default int deleteUserRoleInfo(SysUserRoleEntity userRole) {
        return delete(new QueryWrapper<SysUserRoleEntity>()
                .eq("user_id" , userRole.getUserId())
                .eq("role_id" , userRole.getRoleId()));
    }

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    default int deleteUserRoleInfos(@Param("roleId") Long roleId, @Param("userIds") Long[] userIds) {
        return delete(new QueryWrapper<SysUserRoleEntity>()
                .eq("role_id" , roleId)
                .in("user_id" , userIds));
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    default int countUserRoleByRoleId(Long roleId) {

        return selectCount("role_id" , roleId).intValue();
    }

    /**
     * 批量删除用户和角色关联
     *
     * @param userIds 需要删除的数据ID
     * @return 结果
     */
    void deleteUserRole(Long[] userIds);
}
