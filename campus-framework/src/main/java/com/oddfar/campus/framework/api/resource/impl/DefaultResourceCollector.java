package com.oddfar.campus.framework.api.resource.impl;

import com.oddfar.campus.common.domain.entity.SysResourceEntity;
import com.oddfar.campus.common.domain.model.SysRoleAuth;
import com.oddfar.campus.common.domain.model.SysRoleAuthList;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.api.resource.ResourceCollectorApi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultResourceCollector implements ResourceCollectorApi {

    /**
     * 以资源编码为key，存放资源集合
     */
    private final Map<String, SysResourceEntity> resourceDefinitions = new ConcurrentHashMap<>();

    private final Map<Long, SysRoleAuthList> roleListMap = new ConcurrentHashMap<>();

    @Override
    public void collectResources(List<SysResourceEntity> apiResource) {
        if (apiResource != null && apiResource.size() > 0) {
            for (SysResourceEntity resourceEntity : apiResource) {
                SysResourceEntity alreadyFlag = resourceDefinitions.get(resourceEntity.getResourceCode());
                if (alreadyFlag != null) {
                    throw new RuntimeException("资源扫描过程中存在重复资源！\n已存在资源：" + alreadyFlag + "\n新资源为： " + resourceEntity);
                }
                resourceDefinitions.put(resourceEntity.getResourceCode(), resourceEntity);
            }
        }
    }

    @Override
    public List<SysResourceEntity> getAllResources() {
        Set<Map.Entry<String, SysResourceEntity>> entries = resourceDefinitions.entrySet();
        ArrayList<SysResourceEntity> resourceDefinitions = new ArrayList<>();
        for (Map.Entry<String, SysResourceEntity> entry : entries) {
            resourceDefinitions.add(entry.getValue());
        }
        return resourceDefinitions;
    }

    @Override
    public void setRoleAuthCache(Map<Long, List<SysRoleAuth>> rolePermsMap, Map<Long, List<SysRoleAuth>> roleResourceMap) {
        roleListMap.clear();

        for (Long roleId : rolePermsMap.keySet()) {

            List<SysRoleAuth> list = rolePermsMap.get(roleId);
            //把关于roleId的perms数据建立成set集合
            Set<String> perms = new HashSet<>();
            list.stream().forEach(r -> {
                if (StringUtils.isNotEmpty(r.getPerms())) {
                    perms.add(r.getPerms());
                }
            });

            //如果resMap包含roleId
            if (roleListMap.containsKey(roleId)) {
                SysRoleAuthList sysRoleList = roleListMap.get(roleId);
                if (sysRoleList.getPerms() != null) {
                    //如果存在perms，则添加数据
                    sysRoleList.getPerms().addAll(perms);
                } else {
                    //无数据则直接set
                    sysRoleList.setPerms(perms);
                }
            } else {
                //不包含roleId重新生成
                SysRoleAuthList sysRoleList = new SysRoleAuthList(roleId, perms, null);
                roleListMap.put(roleId, sysRoleList);
            }

        }

        for (Long roleId : roleResourceMap.keySet()) {
            List<SysRoleAuth> list = roleResourceMap.get(roleId);
            //把关于roleId的resource数据建立成set集合
            Set<String> resourceSet = new HashSet<>();


            list.stream().forEach(r -> {
                if (StringUtils.isNotEmpty(r.getResourceCode())) {
                    resourceSet.add(r.getResourceCode());
                }
            });


            //如果map包含roleId
            if (roleListMap.containsKey(roleId)) {
                SysRoleAuthList sysRoleList = roleListMap.get(roleId);
                if (sysRoleList.getResourceCode() != null) {
                    sysRoleList.getResourceCode().addAll(resourceSet);
                } else {
                    sysRoleList.setResourceCode(resourceSet);
                }

            } else {
                SysRoleAuthList sysRoleList = new SysRoleAuthList(roleId, null, resourceSet);
                roleListMap.put(roleId, sysRoleList);
            }

        }

    }

    @Override
    public Map<Long, SysRoleAuthList> getRoleListMap() {
        return roleListMap;
    }

}
