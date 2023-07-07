package com.oddfar.campus.framework.mapper;

import cn.hutool.core.util.ObjectUtil;
import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysMenuEntity;
import com.oddfar.campus.common.domain.model.SysRoleAuth;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMenuMapper extends BaseMapperX<SysMenuEntity> {
    default PageResult<SysMenuEntity> selectPage(SysMenuEntity sysMenuEntity) {

        return selectPage(new LambdaQueryWrapperX<SysMenuEntity>());
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    default List<SysMenuEntity> selectMenuList(SysMenuEntity menu) {

        return selectList(new LambdaQueryWrapperX<SysMenuEntity>()
                .like(ObjectUtil.isNotNull(menu.getMenuName()), SysMenuEntity::getMenuName, menu.getMenuName())
                .eq(ObjectUtil.isNotNull(menu.getVisible()), SysMenuEntity::getVisible, menu.getVisible())
                .eq(ObjectUtil.isNotNull(menu.getStatus()), SysMenuEntity::getStatus, menu.getStatus()));
    }

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<SysMenuEntity> selectMenuListByUserId(SysMenuEntity menu);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    List<SysMenuEntity> selectMenuTreeAll();

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenuEntity> selectMenuTreeByUserId(Long userId);


    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 查询所有角色的权限列表
     *
     * @return SysRolePerms
     */
    List<SysRoleAuth> getMenuPermsAll();

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByUserId(Long userId);


    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId            角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);

    /**
     * 校验菜单名称是否唯一
     */
    default SysMenuEntity checkMenuNameUnique(SysMenuEntity menu) {
        return selectOne(new LambdaQueryWrapperX<SysMenuEntity>()
                .eq(SysMenuEntity::getMenuName, menu.getMenuName())
                .eq(SysMenuEntity::getParentId, menu.getParentId())
        );
    }


}
