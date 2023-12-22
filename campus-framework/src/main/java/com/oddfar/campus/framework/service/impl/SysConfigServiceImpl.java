package com.oddfar.campus.framework.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.oddfar.campus.common.constant.CacheConstants;
import com.oddfar.campus.common.constant.UserConstants;
import com.oddfar.campus.common.core.RedisCache;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysConfigEntity;
import com.oddfar.campus.common.exception.ServiceException;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.mapper.SysConfigMapper;
import com.oddfar.campus.framework.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@Service
public class SysConfigServiceImpl implements SysConfigService {

    @Resource
    private SysConfigMapper configMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        loadingConfigCache();
    }

    @Override
    public PageResult<SysConfigEntity> page(SysConfigEntity sysConfigEntity) {
        return configMapper.selectPage(sysConfigEntity);
    }

    @Override
    public SysConfigEntity selectConfigById(Long configId) {
        SysConfigEntity config = new SysConfigEntity();
        config.setConfigId(configId);
        return configMapper.selectById(config);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue)) {
            return configValue;
        }
        SysConfigEntity config = new SysConfigEntity();
        config.setConfigKey(configKey);
        SysConfigEntity retConfig = configMapper.selectConfig(config);
        if (StringUtils.isNotNull(retConfig)) {
            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public <T> T selectConfigByKey(String configKey, Class<T> clazz) {
        T configValue = redisCache.getCacheObject(getCacheKey(configKey));
        if (ObjectUtil.isNotEmpty(configValue)) {
            return configValue;
        }
        SysConfigEntity config = new SysConfigEntity();
        config.setConfigKey(configKey);
        SysConfigEntity retConfig = configMapper.selectConfig(config);
        if (ObjectUtil.isNotNull(retConfig)) {
            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return Convert.convert(clazz, retConfig.getConfigValue());
        }
        return null;
    }

    @Override
    public <T> T selectConfigByKey(String configKey, Class<T> clazz, T defaultValue) {
        T value = this.selectConfigByKey(configKey, clazz);
        return value == null ? defaultValue : value;
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled() {
        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled)) {
            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    @Override
    public int insertConfig(SysConfigEntity config) {
        int row = configMapper.insert(config);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    @Override
    public int updateConfig(SysConfigEntity config) {
        int row = configMapper.updateById(config);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    @Override
    public void deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            SysConfigEntity config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            configMapper.deleteById(configId);
            redisCache.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfigEntity> configsList = configMapper.selectList();
        for (SysConfigEntity config : configsList) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }


    @Override
    public boolean checkConfigKeyUnique(SysConfigEntity config) {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfigEntity info = configMapper.checkConfigKeyUnique(config);
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
