package com.oddfar.campus.common.domain.model;

import com.oddfar.campus.common.domain.entity.SysRoleResourceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleAuth {

    /**
     * 角色id
     */
    Long roleID;

    /**
     * 权限字符
     */
    String perms;

    /**
     * 资源编码
     */
   String resourceCode;

    public SysRoleAuth(SysRoleResourceEntity roleResourceEntity) {
        this.roleID = roleResourceEntity.getRoleId();
        this.resourceCode = roleResourceEntity.getResourceCode();
    }
}
