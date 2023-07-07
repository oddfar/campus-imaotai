package com.oddfar.campus.framework.api.resource;

import com.oddfar.campus.common.domain.entity.SysResourceEntity;
import com.oddfar.campus.common.domain.model.SysRoleAuth;
import com.oddfar.campus.common.domain.model.SysRoleAuthList;

import java.util.List;
import java.util.Map;

/**
 * 权限资源收集器，搜集本项目中的资源，仅搜集并缓存起来，不持久化
 * 参考 https://gitee.com/stylefeng/guns 项目
 */
public interface ResourceCollectorApi {


    /**
     * 保存所有扫描到的资源
     *
     */
    void collectResources(List<SysResourceEntity> apiResource);


    /**
     * 获取当前运行项目的所有资源
     *
     */
    List<SysResourceEntity> getAllResources();

    /**
     * 设置角色的资源和菜单权限缓存
     * @param rolePermsMap
     * @param roleResourceMap
     */
    void setRoleAuthCache(Map<Long, List<SysRoleAuth>> rolePermsMap, Map<Long, List<SysRoleAuth>> roleResourceMap);

    /**
     * 获取缓存的角色的资源和菜单权限
     */
    Map<Long, SysRoleAuthList> getRoleListMap();

}
