package com.oddfar.campus.framework.mapper;

import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper extends BaseMapperX<SysUserEntity> {

    default PageResult<SysUserEntity> selectPage(SysUserEntity user) {

        return selectPage(new LambdaQueryWrapperX<SysUserEntity>()
                .likeIfPresent(SysUserEntity::getUserName, user.getUserName())
                .likeIfPresent(SysUserEntity::getPhonenumber, user.getPhonenumber())
                .eqIfPresent(SysUserEntity::getStatus, user.getStatus())
                .betweenIfPresent(SysUserEntity::getCreateTime, user.getParams())
        );
    }


    /**
     * 通过用户名查询用户
     *
     * @param userName
     * @return
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
     * 根据条件分页查询已配用户角色列表
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
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    int updateUserAvatar(@Param("userName") String userName, @Param("avatar") String avatar);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    default SysUserEntity checkEmailUnique(String email) {

        return selectOne(new LambdaQueryWrapperX<SysUserEntity>().eq(SysUserEntity::getEmail, email));
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @return 结果
     */
    default SysUserEntity checkPhoneUnique(String phonenumber) {
        return selectOne(new LambdaQueryWrapperX<SysUserEntity>().eq(SysUserEntity::getPhonenumber, phonenumber));
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    int resetUserPwd(@Param("userName") String userName, @Param("password") String password);


    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    default SysUserEntity checkUserNameUnique(String userName) {
        return selectOne(new LambdaQueryWrapperX<SysUserEntity>().eq(SysUserEntity::getUserName, userName));
    }


}