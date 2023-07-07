package com.oddfar.campus.common.domain.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oddfar.campus.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_resource")
public class SysResourceEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId("resource_id")
    private Long resourceId;

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 资源编码
     */
    private String resourceCode;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 类名称
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 资源模块编码，一般为控制器类名排除Controller
     */
    @TableField(exist = false)
    private String  modular_code;

    /**
     * 资源模块名称，一般为控制器名称
     */
    private String modularName;

    /**
     * 资源url
     */
    private String url;

    /**
     * http请求方法
     */
    private String httpMethod;

    /**
     * 资源的业务类型：1-业务类，2-系统类
     */
    private Integer resourceBizType;

    /**
     * 是否需要鉴权：Y-是，N-否
     */
    private String requiredPermissionFlag;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<SysResourceEntity> children = new ArrayList<SysResourceEntity>();
}
