package com.oddfar.campus.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.business.mapper.IUserMapper;
import com.oddfar.campus.business.service.IUserService;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.exception.ServiceException;
import com.oddfar.campus.common.utils.SecurityUtils;
import com.oddfar.campus.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IUserServiceImpl implements IUserService {
    @Autowired
    private IUserMapper iUserMapper;

    @Override
    public PageResult<IUser> page(IUser iUser) {
        Long userId = SecurityUtils.getUserId();
        if (userId != 1) {
            return iUserMapper.selectPage(iUser, userId);
        }
        return iUserMapper.selectPage(iUser);
    }

    @Override
    public int insertIUser(Long mobile, String deviceId, JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");

        IUser user = iUserMapper.selectById(mobile);

        if (user != null) {
            //存在则更新
            IUser iUser = new IUser(mobile, jsonObject);
            iUser.setCreateUser(SecurityUtils.getUserId());
            BeanUtil.copyProperties(iUser, user, "shopType", "minute");
            return iUserMapper.updateById(user);
        } else {
            if (StringUtils.isEmpty(deviceId)) {
                deviceId = UUID.randomUUID().toString().toLowerCase();
            }
            IUser iUser = new IUser(mobile, deviceId, jsonObject);
            iUser.setCreateUser(SecurityUtils.getUserId());
            return iUserMapper.insert(iUser);
        }


    }

    @Override
    public List<IUser> selectReservationUser() {
        return iUserMapper.selectReservationUser();

    }

    @Override
    public List<IUser> selectReservationUserByMinute(int minute) {
        return iUserMapper.selectReservationUserByMinute(minute);
    }

    @Override
    public int insertIUser(IUser iUser) {

        IUser user = iUserMapper.selectById(iUser.getMobile());
        if (user != null) {
            throw new ServiceException("禁止重复添加");
        }

        if (StringUtils.isEmpty(iUser.getDeviceId())) {
            iUser.setDeviceId(UUID.randomUUID().toString().toLowerCase());
        }
        iUser.setCreateUser(SecurityUtils.getUserId());
        return iUserMapper.insert(iUser);
    }

    @Override
    public int updateIUser(IUser iUser) {
        checkOwner(iUser);
        return iUserMapper.updateById(iUser);
    }

    @Override
    public void checkOwner(IUser iUser) {
        if (iUser == null) {
            throw new ServiceException("用户不存在");
        }
        Long userId = SecurityUtils.getUserId();
        if (userId != 1 && !userId.equals(iUser.getCreateUser())) {
            throw new ServiceException("只能操作自己创建的用户");
        }
    }

    @Override
    @Async
    public void updateUserMinuteBatch() {
        Long userCount = iUserMapper.selectCount();
        if (userCount > 60) {
            iUserMapper.updateUserMinuteEven();
        }else {
            iUserMapper.updateUserMinuteBatch();
        }
    }

    @Override
    public int deleteIUser(Long[] iUserId) {
        for (Long mobile : iUserId) {
            IUser iUser = iUserMapper.selectById(mobile);
            checkOwner(iUser);
        }
        return iUserMapper.deleteIUser(iUserId);
    }
}
