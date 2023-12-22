package com.oddfar.campus.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oddfar.campus.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 配置实体类
 *
 * @author oddfar
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfigEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("config_id")
    private Long configId;

    /**
     * 参数名称
     */
    @NotBlank(message = "参数名称不能为空")
    @Size(min = 0, max = 100, message = "参数名称不能超过100个字符")
    private String configName;

    /**
     * 参数键名
     */
    @NotBlank(message = "参数键名不能为空")
    @Size(min = 0, max = 100, message = "参数键名长度不能超过100个字符")
    private String configKey;

    /**
     * 参数键值
     */
    @NotBlank(message = "参数键值不能为空")
    @Size(min = 0, max = 500, message = "参数键值长度不能超过500个字符")
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    @NotBlank(message = "系统内置不能为空")
    private String configType;

    /**
     * 所属分类的编码
     */
    @NotBlank(message = "所属分类的编码不能为空")
    private String groupCode;

    /**
     * 备注
     */
    private String remark;
}
