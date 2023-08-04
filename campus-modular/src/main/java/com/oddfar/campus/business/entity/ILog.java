package com.oddfar.campus.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * I茅台日志 i_log
 *
 * @author oddfar
 * @date 2023-08-01
 */
@Data
@TableName("i_log")
public class ILog {

    private static final long serialVersionUID = 1L;

    private static final Integer PAGE_NUM = 1;
    private static final Integer PAGE_SIZE = 10;

    /**
     * 日志主键
     */
    @TableId("log_id")
    private Long logId;

    /**
     * 日志记录内容
     */
    private String logContent;

    /**
     * 操作人员手机号
     */
    private Long mobile;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operTime;

    /**
     * 上级人
     */
    private Long createUser;

    @TableField(exist = false)
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    @TableField(exist = false)
    @JsonIgnore
    private Integer pageNum = PAGE_NUM;

    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 100, message = "每页条数最大值为 100")
    @TableField(exist = false)
    @JsonIgnore
    private Integer pageSize = PAGE_SIZE;
}
