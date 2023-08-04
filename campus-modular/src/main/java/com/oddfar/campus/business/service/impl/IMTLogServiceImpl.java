package com.oddfar.campus.business.service.impl;

import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.business.mapper.ILogMapper;
import com.oddfar.campus.business.service.IMTLogService;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class IMTLogServiceImpl implements IMTLogService {

    @Autowired
    private ILogMapper logMapper;

    @Override
    public PageResult<ILog> page(ILog iLog) {
        Long userId = SecurityUtils.getUserId();
        if (userId != 1) {
            return logMapper.selectPage(iLog, userId);
        }
        return logMapper.selectPage(iLog);
    }


    @Override
    public int deleteLogByIds(Long[] operIds) {
        return logMapper.deleteBatchIds(Arrays.asList(operIds));
    }

    @Override
    public void cleanLog() {
        logMapper.cleanLog();
    }

    @Override
    public int insertLog(ILog iLog) {
        return logMapper.insert(iLog);
    }
}
