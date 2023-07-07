package com.oddfar.campus.admin.controller.system;

import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.domain.entity.SysConfigEntity;
import com.oddfar.campus.common.enums.ResBizTypeEnum;
import com.oddfar.campus.framework.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 配置管理
 */
@RestController
@RequestMapping("/system/config")
@ApiResource(name = "参数配置管理", resBizType = ResBizTypeEnum.SYSTEM)
public class SysConfigController {
    @Autowired
    private SysConfigService configService;

    @GetMapping(value = "page", name = "参数配置管理-分页")
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    public R page(SysConfigEntity sysConfigEntity) {
        PageResult<SysConfigEntity> page = configService.page(sysConfigEntity);

        return R.ok().put(page);
    }

    @GetMapping(value = "{id}", name = "参数配置管理-查询id信息")
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    public R getInfo(@PathVariable("id") Long id) {
        SysConfigEntity entity = configService.selectConfigById(id);

        return R.ok().put(entity);
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey:.+}")
    public R getConfigKey(@PathVariable String configKey) {
        return R.ok(configService.selectConfigByKey(configKey));
    }

    @PostMapping(name = "参数配置管理-新增")
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    public R add(@Validated @RequestBody SysConfigEntity config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return R.error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        return R.ok(configService.insertConfig(config));

    }

    @PutMapping(name = "参数配置管理-修改")
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    public R edit(@Validated @RequestBody SysConfigEntity config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return R.error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        return R.ok(configService.updateConfig(config));

    }

    @DeleteMapping(value = "/{configIds}", name = "参数配置管理-删除")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    public R remove(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);

        return R.ok();
    }

    /**
     * 刷新参数缓存
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @DeleteMapping(value = "/refreshCache", name = "参数配置管理-刷新缓存")
    public R refreshCache() {
        configService.resetConfigCache();
        return R.ok();
    }
}