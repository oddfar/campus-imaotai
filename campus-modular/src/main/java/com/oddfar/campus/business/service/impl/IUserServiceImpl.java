package com.oddfar.campus.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.business.mapper.IUserMapper;
import com.oddfar.campus.business.service.IUserService;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.exception.ServiceException;
import com.oddfar.campus.common.utils.SecurityUtils;
import com.oddfar.campus.common.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
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
        if (SecurityUtils.getUserId() != 1 && !iUser.getCreateUser().equals(SecurityUtils.getUserId())) {
            throw new ServiceException("只能修改自己创建的用户");
        }
        return iUserMapper.updateById(iUser);
    }

    @Override
    @Async
    public void updateUserMinuteBatch() {
        Long userCount = iUserMapper.selectCount();
        if (userCount < 60) {
            // 如果用户数小于60，查询全部用户并更新
            batchUpdateUserMinute();
        } else {
            // randomMinute 0:随机，1:不随机
            //原逻辑批量处理为什么没有加：random_minute = "0"限制条件？为了保持原逻辑 我这里也没加
            updateUserMinuteEven();
        }
    }
    private void batchUpdateUserMinute() {
        //使用lambda查询
        LambdaQueryWrapper<IUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //0:随机，1:不随机
        lambdaQueryWrapper.eq(IUser::getRandomMinute, "0");
        List<IUser> users = iUserMapper.selectList(lambdaQueryWrapper);
        if(CollectionUtils.isEmpty(users)){
            return;
        }
        // 使用 Hutool 生成1-50之间的随机数
        for (IUser user : users) {
            user.setMinute(RandomUtil.randomInt(1, 51));  // 随机生成 1-50 之间的数字
        }
        iUserMapper.batchUpdateUserMinute(users);  // 批量更新数据库
    }

    private void updateUserMinuteEven() {
        Long maxId = 0L;
        // 一次查询1000条数据
        int limitCount = 1000;

        while (true) {
            // 每次查询1000条数据，使用 maxId 进行分页
            List<IUser> users = iUserMapper.selectUsersBatch(maxId, limitCount);

            // 如果查询结果为空，终止循环
            if (CollectionUtils.isEmpty(users)) {
                break;
            }

            // 更新这批用户的minute字段，分配1-50的数字
            for (IUser user : users) {
                user.setMinute(RandomUtil.randomInt(1, 51));  // 使用 Hutool 生成随机数
            }

            // 批量更新用户的 minute 字段
            iUserMapper.batchUpdateUserMinute(users);
            if(users.size() < limitCount){
                break;
            }

            // 更新 maxId 为当前批次中的最大 id
            maxId = users.get(users.size() - 1).getUserId();  // 获取当前批次最后一条记录的 ID，作为下次查询的起始点
        }
    }


    @Override
    public int deleteIUser(Long[] iUserId) {
        return iUserMapper.deleteIUser(iUserId);
    }
}
