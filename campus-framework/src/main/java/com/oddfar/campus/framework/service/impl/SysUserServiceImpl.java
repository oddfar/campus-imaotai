package com.oddfar.campus.framework.service.impl;

import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysRoleEntity;
import com.oddfar.campus.common.domain.entity.SysUserEntity;
import com.oddfar.campus.common.domain.entity.SysUserRoleEntity;
import com.oddfar.campus.common.exception.ServiceException;
import com.oddfar.campus.common.utils.SecurityUtils;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.api.sysconfig.ConfigExpander;
import com.oddfar.campus.framework.mapper.SysRoleMapper;
import com.oddfar.campus.framework.mapper.SysUserMapper;
import com.oddfar.campus.framework.mapper.SysUserRoleMapper;
import com.oddfar.campus.framework.service.SysRoleService;
import com.oddfar.campus.framework.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysRoleService roleService;

    @Override
    public PageResult<SysUserEntity> page(SysUserEntity sysUserEntity) {
        return userMapper.selectPage(sysUserEntity);
    }

    @Override
    public SysUserEntity selectUserByUserName(String userName) {
        SysUserEntity userEntity = userMapper.selectUserByUserName(userName);
        if ( userEntity != null &&StringUtils.isEmpty(userEntity.getAvatar())) {
            userEntity.setAvatar(ConfigExpander.getUserDefaultAvatar());
        }
        return userEntity;
    }

    @Override
    public SysUserEntity selectUserById(Long userId) {
        SysUserEntity userEntity = userMapper.selectUserById(userId);
        if (userEntity != null && StringUtils.isEmpty(userEntity.getAvatar())) {
            userEntity.setAvatar(ConfigExpander.getUserDefaultAvatar());
        }
        return userEntity;
    }

    @Override
    public List<SysUserEntity> selectAllocatedList(SysUserEntity user) {
        return userMapper.selectAllocatedList(user);
    }

    @Override
    public List<SysUserEntity> selectUnallocatedList(SysUserEntity user) {
        return userMapper.selectUnallocatedList(user);
    }

    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRoleEntity> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRoleEntity::getRoleName).collect(Collectors.joining(","));
    }

    @Override
    public boolean registerUser(SysUserEntity user) {
        return userMapper.insert(user) > 0;
    }

    @Override
    @Transactional
    public int insertUser(SysUserEntity user) {
        if (StringUtils.isNotEmpty(user.getUserName())
                && !checkUserNameUnique(user)) {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && !(checkPhoneUnique(user))) {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail())
                && !(checkEmailUnique(user))) {
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        // 新增用户信息
        int rows = userMapper.insert(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    @Override
    @Transactional
    public int updateUser(SysUserEntity user) {

        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        return userMapper.updateById(user);
    }

    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUserEntity(userId));
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        return userMapper.deleteBatchIds(Arrays.asList(userIds));
    }


    @Override
    public int updateUserProfile(SysUserEntity user) {
        return userMapper.updateById(user);
    }

    @Override
    public int updateUserStatus(SysUserEntity user) {
        return userMapper.updateById(user);
    }

    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    @Override
    public int resetPwd(SysUserEntity user) {
        return userMapper.updateById(user);
    }

    @Override
    public void checkUserAllowed(SysUserEntity user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    @Override
    public void insertUserAuth(Long userId, Set<String> roleKey) {
        //查询现有的权限字符
        Set<String> roleSet = roleService.selectRolePermissionByUserId(userId);
        roleKey.addAll(roleSet);

        List<SysRoleEntity> sysRoleList = roleMapper.selectRoleListByKey(roleKey);
        List<Long> roleIds = sysRoleList.stream().map(SysRoleEntity::getRoleId).collect(Collectors.toList());

        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds.toArray(new Long[0]));

    }

    @Override
    public int resetUserPwd(String userName, String password) {
        return userMapper.resetUserPwd(userName, password);
    }

    @Override
    public boolean checkUserNameUnique(SysUserEntity user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUserEntity info = userMapper.checkUserNameUnique(user.getUserName());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkPhoneUnique(SysUserEntity user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUserEntity info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkEmailUnique(SysUserEntity user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUserEntity info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return false;
        }
        return true;
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUserEntity user) {
        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }


    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (StringUtils.isNotEmpty(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRoleEntity> list = new ArrayList<SysUserRoleEntity>(roleIds.length);
            for (Long roleId : roleIds) {
                SysUserRoleEntity ur = new SysUserRoleEntity();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.insertBatch(list);
        }
    }
}
