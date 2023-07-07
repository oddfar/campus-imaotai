package com.oddfar.campus.framework.listener;


import com.oddfar.campus.common.domain.entity.SysResourceEntity;
import com.oddfar.campus.framework.api.resource.ResourceCollectorApi;
import com.oddfar.campus.framework.service.SysResourceService;
import com.oddfar.campus.framework.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 监听项目初始化完毕，执行的操作
 */
@Component
public class ReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private SysResourceService resourceService;
    @Autowired
    private SysRoleService roleService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //导入资源数据
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();

        //清空数据
        resourceService.truncateResource();

        // 获取当前系统的所有资源
        ResourceCollectorApi resourceCollectorApi = applicationContext.getBean(ResourceCollectorApi.class);
        List<SysResourceEntity> allResources = resourceCollectorApi.getAllResources();
        //添加api接口资源到数据库
        resourceService.saveBatch(allResources);

        //把用户资源和权限缓存
        roleService.resetRoleAuthCache();
    }


}
