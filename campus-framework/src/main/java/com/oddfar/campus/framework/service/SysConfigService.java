package com.oddfar.campus.framework.service;

import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysConfigEntity;

public interface SysConfigService {

    PageResult<SysConfigEntity> page(SysConfigEntity sysConfigEntity);

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    SysConfigEntity selectConfigById(Long configId);

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    String selectConfigByKey(String configKey);

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey
     * @param clazz     转换的类
     * @param <T>
     * @return
     */
    <T> T selectConfigByKey(String configKey, Class<T> clazz);

    /**
     * 根据键名查询参数配置信息
     * 查询的值为null则返回defaultValue
     *
     * @param configKey
     * @param clazz
     * @param defaultValue 默认的内容
     * @param <T>
     * @return
     */
    <T> T selectConfigByKey(String configKey, Class<T> clazz, T defaultValue);


    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    boolean selectCaptchaEnabled();

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    int insertConfig(SysConfigEntity config);

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    int updateConfig(SysConfigEntity config);

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    void deleteConfigByIds(Long[] configIds);

    /**
     * 加载参数缓存数据
     */
    void loadingConfigCache();

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果 true为唯一
     */
    boolean checkConfigKeyUnique(SysConfigEntity config);

    /**
     * 清空参数缓存数据
     */
    void clearConfigCache();

    /**
     * 重置参数缓存数据
     */
    void resetConfigCache();


}
