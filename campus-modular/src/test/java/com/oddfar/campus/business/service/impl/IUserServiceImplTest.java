package com.oddfar.campus.business.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.business.mapper.IUserMapper;
import com.oddfar.campus.common.constant.UserConstants;
import com.oddfar.campus.common.domain.entity.SysUserEntity;
import com.oddfar.campus.common.domain.model.LoginUser;
import com.oddfar.campus.common.exception.ServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 账号属主校验测试 (#356)
 */
public class IUserServiceImplTest {

    private final IUserServiceImpl service = new IUserServiceImpl();
    private final IUserMapper mapper = mock(IUserMapper.class);

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(service, "iUserMapper", mapper);
    }

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

    @Test
    public void testUpdateRejectForgedCreateUser() {
        //请求体伪造 createUser=2L，但数据库里属主是 3L，必须拒绝且不落库
        loginAs(2L);
        IUser dbUser = new IUser();
        dbUser.setMobile(111L);
        dbUser.setCreateUser(3L);
        when(mapper.selectById(111L)).thenReturn(dbUser);
        IUser body = new IUser();
        body.setMobile(111L);
        body.setCreateUser(2L);
        assertThrows(ServiceException.class, () -> service.updateIUser(body));
        verify(mapper, never()).updateById(any());
    }

    @Test
    public void testUpdateByOwnerKeepsDbOwner() {
        loginAs(2L);
        IUser dbUser = new IUser();
        dbUser.setMobile(111L);
        dbUser.setCreateUser(2L);
        when(mapper.selectById(111L)).thenReturn(dbUser);
        IUser body = new IUser();
        body.setMobile(111L);
        body.setCreateUser(9L);
        assertDoesNotThrow(() -> service.updateIUser(body));
        ArgumentCaptor<IUser> captor = ArgumentCaptor.forClass(IUser.class);
        verify(mapper).updateById(captor.capture());
        //属主以数据库为准，不可被请求体改写
        assertEquals(Long.valueOf(2L), captor.getValue().getCreateUser());
    }

    @Test
    public void testRefreshTokenPreservesNotifyConfig() {
        //刷新 token(重复登录)不得抹掉已有的通知渠道配置
        loginAs(2L);
        IUser existing = new IUser();
        existing.setMobile(111L);
        existing.setCreateUser(2L);
        existing.setDingWebhook("https://oapi.dingtalk.com/robot/send?access_token=abc");
        existing.setDingSecret("SEC123");
        existing.setMailAddr("a@b.com");
        existing.setPushPlusToken("pptoken");
        when(mapper.selectById(111L)).thenReturn(existing);
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("userId", 999);
        data.put("token", "tk");
        data.put("cookie", "ck");
        json.put("data", data);
        service.insertIUser(111L, "dev", json);
        ArgumentCaptor<IUser> captor = ArgumentCaptor.forClass(IUser.class);
        verify(mapper).updateById(captor.capture());
        IUser updated = captor.getValue();
        assertEquals("https://oapi.dingtalk.com/robot/send?access_token=abc", updated.getDingWebhook());
        assertEquals("SEC123", updated.getDingSecret());
        assertEquals("a@b.com", updated.getMailAddr());
        assertEquals("pptoken", updated.getPushPlusToken());
    }
}
