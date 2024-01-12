package com.oddfar.campus.framework.mapper;

import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysResourceEntity;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

public interface SysResourceMapper extends BaseMapperX<SysResourceEntity> {
    default PageResult<SysResourceEntity> selectPage(SysResourceEntity resource) {

        return selectPage(new LambdaQueryWrapperX<SysResourceEntity>()
                .betweenIfPresent(SysResourceEntity::getCreateTime, resource.getParams()));
    }


    /**
     * 清空 sys_resource 数据库
     */
    @Update("truncate table sys_resource")
    void truncateResource();

    /**
     * 根据角色ID查询资源编码列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    Set<String> selectResourceCodeByRoleId(Long roleId);

    /**
     * 根据角色ID查询资源树信息
     *
     * @param roleId 角色ID
     * @return 选中接口资源列表
     */
    List<Long> selectResourceListByRoleId(Long roleId);

    /**
     * 根据用户查询api资源列表
     *
     * @param resource
     * @return
     */
    List<SysResourceEntity> selectResourceListByUserId(SysResourceEntity resource);

    /**
     * 查询资源列表
     *
     * @param resource
     * @return
     */
    List<SysResourceEntity> selectResourceList(SysResourceEntity resource);


}
