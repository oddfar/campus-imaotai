package com.oddfar.campus.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysRoleEntity;
import com.oddfar.campus.common.domain.entity.SysRoleMenuEntity;
import com.oddfar.campus.common.domain.entity.SysUserRoleEntity;
import com.oddfar.campus.common.domain.model.SysRoleAuth;
import com.oddfar.campus.common.exception.ServiceException;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.api.resource.ResourceCollectorApi;
import com.oddfar.campus.framework.mapper.SysRoleMapper;
import com.oddfar.campus.framework.mapper.SysRoleMenuMapper;
import com.oddfar.campus.framework.mapper.SysUserRoleMapper;
import com.oddfar.campus.framework.service.SysMenuService;
import com.oddfar.campus.framework.service.SysResourceService;
import com.oddfar.campus.framework.service.SysRoleService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRoleEntity> implements SysRoleService {

    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysUserRoleMapper userRoleMapper;
    @Resource
    private SysRoleMenuMapper roleMenuMapper;
    @Resource
    private SysMenuService menuService;
    @Resource
    private SysResourceService resourceService;

    @Resource
    ApplicationContext applicationContext;

    @Override
    public PageResult<SysRoleEntity> page(SysRoleEntity sysRoleEntity) {
        return roleMapper.selectPage(sysRoleEntity);
    }

    @Override
    public List<SysRoleEntity> selectRoleList(SysRoleEntity role) {
        return roleMapper.selectRoleList(role);
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRoleEntity> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRoleEntity perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }


    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRoleEntity selectRoleById(Long roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    @Override
    public List<SysRoleEntity> selectRoleAll() {
        return this.selectRoleList(new SysRoleEntity());
//        return SpringUtils.getAopProxy(this).selectRoleList(new SysRoleEntity());
    }

    @Override
    public List<SysRoleEntity> selectRolesByUserId(Long userId) {
        List<SysRoleEntity> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        List<SysRoleEntity> roles = selectRoleAll();
        for (SysRoleEntity role : roles) {
            for (SysRoleEntity userRole : userRoles) {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }


    @Override
    @Transactional
    public int insertRole(SysRoleEntity role) {
        // 新增角色信息
        roleMapper.insert(role);
        return insertRoleMenu(role);
    }

    @Override
    public int updateRole(SysRoleEntity role) {
        // 修改角色信息
        roleMapper.updateById(role);
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());

        return insertRoleMenu(role);
    }

    @Override
    public int updateRoleStatus(SysRoleEntity role) {
        return roleMapper.updateById(role);
    }

    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new SysRoleEntity(roleId));
            SysRoleEntity role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenu(roleIds);
        // 删除角色
        return roleMapper.deleteBatchIds(Arrays.asList(roleIds));
    }

    @Override
    public int deleteAuthUser(SysUserRoleEntity userRole) {
        return userRoleMapper.deleteUserRoleInfo(userRole);
    }

    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
    }

    @Override
    public boolean insertAuthUsers(Long roleId, Long[] userIds) {
        // 新增用户与角色管理
        List<SysUserRoleEntity> list = new ArrayList<SysUserRoleEntity>();
        for (Long userId : userIds) {
            SysUserRoleEntity ur = new SysUserRoleEntity();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }

        return this.saveBatch(list);
    }

    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    @Override
    public boolean checkRoleNameUnique(SysRoleEntity role) {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRoleEntity info = roleMapper.checkRoleNameUnique(role.getRoleName());
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRoleEntity role) {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRoleEntity info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public void checkRoleAllowed(SysRoleEntity role) {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    @Override
    public void resetRoleAuthCache() {
        //把用户资源和权限缓存
        Map<Long, List<SysRoleAuth>> rolePermsMap = menuService.selectMenuPermsAll();
        Map<Long, List<SysRoleAuth>> roleResourceMap = resourceService.selectSysRoleAuthAll().stream().collect(Collectors.groupingBy(SysRoleAuth::getRoleID));

        ResourceCollectorApi resourceCollectorApi = applicationContext.getBean(ResourceCollectorApi.class);
        resourceCollectorApi.setRoleAuthCache(rolePermsMap, roleResourceMap);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public int insertRoleMenu(SysRoleEntity role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenuEntity> list = new ArrayList<SysRoleMenuEntity>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenuEntity rm = new SysRoleMenuEntity();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }


}
