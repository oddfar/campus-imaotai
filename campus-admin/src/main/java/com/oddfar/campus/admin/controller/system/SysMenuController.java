package com.oddfar.campus.admin.controller.system;

import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.constant.UserConstants;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.domain.entity.SysMenuEntity;
import com.oddfar.campus.common.enums.ResBizTypeEnum;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.oddfar.campus.common.utils.SecurityUtils.getUserId;

@RestController
@RequestMapping("/system/menu")
@ApiResource(name = "菜单管理", resBizType = ResBizTypeEnum.SYSTEM)
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping(value = "/list", name = "菜单管理-分页")
    public R list(SysMenuEntity menu) {
        List<SysMenuEntity> menus = menuService.selectMenuList(menu, getUserId());
        return R.ok(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}", name = "菜单管理-查询")
    public R getInfo(@PathVariable Long menuId) {
        return R.ok(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping(value = "/treeselect", name = "菜单管理-获取菜单下拉树列表")
    public R treeSelect(SysMenuEntity menu) {
        List<SysMenuEntity> menus = menuService.selectMenuList(menu, getUserId());
        return R.ok(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}", name = "菜单管理-加载对应角色菜单列表树")
    public R roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        List<SysMenuEntity> menus = menuService.selectMenuList(getUserId());
        R ajax = R.ok();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @PostMapping(name = "菜单管理-新增")
    public R add(@Validated @RequestBody SysMenuEntity menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return R.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        return R.ok(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @PutMapping(name = "菜单管理-修改")
    public R edit(@Validated @RequestBody SysMenuEntity menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return R.error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return R.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        return R.ok(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @DeleteMapping(value = "/{menuId}", name = "菜单管理-删除")
    public R remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return R.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return R.error("菜单已分配,不允许删除");
        }
        return R.ok(menuService.deleteMenuById(menuId));
    }


}
