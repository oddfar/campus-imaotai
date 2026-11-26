package com.oddfar.campus.business.service.impl;

import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.common.constant.UserConstants;
import com.oddfar.campus.common.domain.entity.SysUserEntity;
import com.oddfar.campus.common.domain.model.LoginUser;
import com.oddfar.campus.common.exception.ServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 账号属主校验测试 (#356)
 */
public class IUserServiceImplTest {

    private final IUserServiceImpl service = new IUserServiceImpl();

    private void loginAs(long userId) {
        SysUserEntity sysUser = new SysUserEntity();
        sysUser.setStatus(UserConstants.NORMAL);
        LoginUser loginUser = new LoginUser(userId, sysUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null));
    }

    @AfterEach
    public void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCheckOwnerPassForOwner() {
        loginAs(2L);
        IUser iUser = new IUser();
        iUser.setCreateUser(2L);
        assertDoesNotThrow(() -> service.checkOwner(iUser));
    }

    @Test
    public void testCheckOwnerRejectNonOwner() {
        loginAs(2L);
        IUser iUser = new IUser();
        iUser.setCreateUser(3L);
        assertThrows(ServiceException.class, () -> service.checkOwner(iUser));
    }

    @Test
    public void testCheckOwnerAdminPassAny() {
        loginAs(1L);
        IUser iUser = new IUser();
        iUser.setCreateUser(3L);
        assertDoesNotThrow(() -> service.checkOwner(iUser));
    }

    @Test
    public void testCheckOwnerNullUser() {
        loginAs(2L);
        assertThrows(ServiceException.class, () -> service.checkOwner(null));
    }

    @Test
    public void testCheckOwnerNullCreateUser() {
        loginAs(2L);
        IUser iUser = new IUser();
        assertThrows(ServiceException.class, () -> service.checkOwner(iUser));
    }
}
