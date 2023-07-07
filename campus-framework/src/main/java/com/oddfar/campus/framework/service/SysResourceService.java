package com.oddfar.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.TreeSelect;
import com.oddfar.campus.common.domain.entity.SysResourceEntity;
import com.oddfar.campus.common.domain.model.SysRoleAuth;

import java.util.List;
import java.util.Set;

public interface SysResourceService extends IService<SysResourceEntity> {

    PageResult<SysResourceEntity> page(SysResourceEntity sysResourceEntity);


    /**
     * 新增接口资源信息
     *
     * @param resource
     * @return
     */
    int insertResource(SysResourceEntity resource);

    /**
     * 清空 sys_resource 数据库
     */
    void truncateResource();

    /**
     * 根据角色ID查询资源编码列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    Set<String> selectResourceCodeByRoleId(Long roleId);


    /**
     * 根据用户id查询api资源列表
     *
     * @param userId
     * @return
     */
    List<SysResourceEntity> selectApiResourceList(Long userId);

    /**
     * 查询所有SysRoleAuth关系,关于resource的
     */
    List<SysRoleAuth> selectSysRoleAuthAll();

    /**
     * 根据用户id查询api资源列表
     *
     * @param userId
     * @return
     */
    List<SysResourceEntity> selectApiResourceList(SysResourceEntity resource, Long userId);

    /**
     * 根据角色ID查询资源树信息
     *
     * @param roleId 角色ID
     * @return 选中接口资源列表
     */
    List<Long> selectResourceListByRoleId(Long roleId);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param resources 资源列表
     * @return 下拉树结构列表
     */
    List<TreeSelect> buildResourceTreeSelect(List<SysResourceEntity> resources);

    /**
     * 修改角色
     *
     * @param roleId
     * @param resourceIds
     */
    void editRoleResource(Long roleId, Long[] resourceIds);
}
