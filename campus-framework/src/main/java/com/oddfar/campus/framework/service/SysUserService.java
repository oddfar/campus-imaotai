package com.oddfar.campus.framework.service;

import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysUserEntity;

import java.util.List;
import java.util.Set;


public interface SysUserService {

    PageResult<SysUserEntity> page(SysUserEntity sysUserEntity);


    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUserEntity selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUserEntity selectUserById(Long userId);

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUserEntity> selectAllocatedList(SysUserEntity user);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUserEntity> selectUnallocatedList(SysUserEntity user);

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserRoleGroup(String userName);

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
     boolean registerUser(SysUserEntity user);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int insertUser(SysUserEntity user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(SysUserEntity user);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUserProfile(SysUserEntity user);

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUserStatus(SysUserEntity user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
     boolean updateUserAvatar(String userName, String avatar);

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    int resetPwd(SysUserEntity user);

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    void checkUserAllowed(SysUserEntity user);

    /**
     * 用户授权角色(先删除再添加)
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    void insertUserAuth(Long userId, Long[] roleIds);

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleKey 角色权限字符串
     */
    void insertUserAuth(Long userId, Set<String> roleKey);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
     int resetUserPwd(String userName, String password);

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
     boolean checkUserNameUnique(SysUserEntity user);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果 true为唯一
     */
    boolean checkPhoneUnique(SysUserEntity user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果 true为唯一
     */
    boolean checkEmailUnique(SysUserEntity user);


}