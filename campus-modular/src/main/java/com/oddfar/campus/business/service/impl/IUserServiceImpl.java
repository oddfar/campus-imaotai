package com.oddfar.campus.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.business.mapper.IUserMapper;
import com.oddfar.campus.business.service.IUserService;
import com.oddfar.campus.common.domain.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IUserServiceImpl implements IUserService {
    @Autowired
    private IUserMapper iUserMapper;

    @Override
    public PageResult<IUser> page(IUser iUser) {
        return iUserMapper.selectPage(iUser);
    }

    @Override
    public int insertIUser(Long mobile, JSONObject jsonObject) {

        JSONObject data = jsonObject.getJSONObject("data");

        IUser user = iUserMapper.selectById(mobile);

        if (user != null) {
            //存在则更新
            IUser iUser = new IUser(mobile, jsonObject);
            BeanUtil.copyProperties(iUser, user);

            return iUserMapper.updateById(user);
        } else {
            IUser iUser = new IUser(mobile, jsonObject);

            return iUserMapper.insert(iUser);
        }


    }

    @Override
    public List<IUser> selectReservationUser() {
       return iUserMapper.selectReservationUser();

    }

    @Override
    public int insertIUser(IUser iUser) {

        return iUserMapper.insert(iUser);
    }

    @Override
    public int updateIUser(IUser iUser) {
        return iUserMapper.updateById(iUser);
    }
}
