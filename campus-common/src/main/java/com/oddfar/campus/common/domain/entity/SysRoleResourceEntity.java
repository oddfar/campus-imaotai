package com.oddfar.campus.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色与接口资源关系表
 */
@Data
@TableName("sys_role_resource")
public class SysRoleResourceEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 资源编码
     */
    private String resourceCode;

    /**
     * 角色ID
     */
    @TableId(type = IdType.INPUT)
    private Long roleId;
}
