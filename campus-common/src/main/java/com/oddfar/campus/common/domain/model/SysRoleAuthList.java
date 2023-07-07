package com.oddfar.campus.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleAuthList {

    /**
     * 角色id
     */
    Long roleID;

    /**
     * 权限字符
     */
    Set<String> perms;

    /**
     * 资源编码
     */
    Set<String> resourceCode;
}
