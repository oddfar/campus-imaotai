package com.oddfar.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oddfar.campus.common.constant.Constants;
import com.oddfar.campus.common.constant.UserConstants;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.TreeSelect;
import com.oddfar.campus.common.domain.entity.SysMenuEntity;
import com.oddfar.campus.common.domain.entity.SysRoleEntity;
import com.oddfar.campus.common.domain.entity.SysRoleMenuEntity;
import com.oddfar.campus.common.domain.entity.SysUserEntity;
import com.oddfar.campus.common.domain.model.SysRoleAuth;
import com.oddfar.campus.common.domain.vo.RouterVo;
import com.oddfar.campus.common.utils.MetaVo;
import com.oddfar.campus.common.utils.SecurityUtils;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.mapper.SysMenuMapper;
import com.oddfar.campus.framework.mapper.SysRoleMapper;
import com.oddfar.campus.framework.mapper.SysRoleMenuMapper;
import com.oddfar.campus.framework.service.SysMenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Resource
    private SysMenuMapper menuMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenuEntity> selectMenuList(Long userId) {
        return selectMenuList(new SysMenuEntity(), userId);
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenuEntity> selectMenuList(SysMenuEntity menu, Long userId) {
        List<SysMenuEntity> menuList = null;
        // 管理员显示所有菜单信息
        if (SysUserEntity.isAdmin(userId)) {
            menuList = menuMapper.selectMenuList(menu);
        } else {
            menu.getParams().put("userId", userId);
            menuList = menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenuEntity> selectMenuTreeByUserId(Long userId) {
        List<SysMenuEntity> menus = null;
        if (SecurityUtils.isAdmin(userId)) {
            menus = menuMapper.selectMenuTreeAll();
        } else {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, 0);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        SysRoleEntity role = roleMapper.selectRoleById(roleId);
        return menuMapper.selectMenuListByRoleId(roleId, role.isMenuCheckStrictly());
    }

    @Override
    public SysMenuEntity selectMenuById(Long menuId) {
        return menuMapper.selectById(menuId);
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenuEntity> menus) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenuEntity menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
            List<SysMenuEntity> cMenus = menu.getChildren();
            if (!cMenus.isEmpty() && cMenus.size() > 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenuEntity> menus) {
        List<SysMenuEntity> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenuEntity> buildMenuTree(List<SysMenuEntity> menus) {
        List<SysMenuEntity> returnList = new ArrayList<SysMenuEntity>();
        List<Long> tempList = new ArrayList<Long>();
        for (SysMenuEntity dept : menus) {
            tempList.add(dept.getMenuId());
        }
        for (Iterator<SysMenuEntity> iterator = menus.iterator(); iterator.hasNext(); ) {
            SysMenuEntity menu = (SysMenuEntity) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId) {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public Map<Long, List<SysRoleAuth>> selectMenuPermsAll() {
        List<SysRoleAuth> sysRolePerms = menuMapper.getMenuPermsAll();
        //根据roleId分组
        return sysRolePerms.stream().collect(Collectors.groupingBy(SysRoleAuth::getRoleID));
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public int insertMenu(SysMenuEntity menu) {
        return menuMapper.insert(menu);
    }

    @Override
    public int updateMenu(SysMenuEntity menu) {
        return menuMapper.updateById(menu);
    }

    @Override
    public int deleteMenuById(Long menuId) {
        return menuMapper.deleteById(menuId);
    }

    @Override
    public boolean checkMenuExistRole(Long menuId) {
        Long result = roleMenuMapper.selectCount(new QueryWrapper<SysRoleMenuEntity>().eq("menu_id", menuId)   );
        return result > 0;
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        Long result = menuMapper.selectCount(
                new LambdaQueryWrapperX<SysMenuEntity>().eq(SysMenuEntity::getParentId, menuId));

        return result > 0;
    }

    @Override
    public boolean checkMenuNameUnique(SysMenuEntity menu) {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        SysMenuEntity info = menuMapper.checkMenuNameUnique(menu);
        if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue()) {
            return false;
        }
        return true;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenuEntity menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(SysMenuEntity menu) {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenuEntity menu) {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(SysMenuEntity menu) {
        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenuEntity menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysMenuEntity menu) {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenuEntity> getChildPerms(List<SysMenuEntity> list, int parentId) {
        List<SysMenuEntity> returnList = new ArrayList<SysMenuEntity>();
        for (Iterator<SysMenuEntity> iterator = list.iterator(); iterator.hasNext(); ) {
            SysMenuEntity t = (SysMenuEntity) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SysMenuEntity> list, SysMenuEntity t) {
        // 得到子节点列表
        List<SysMenuEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenuEntity tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenuEntity> list, SysMenuEntity t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenuEntity> getChildList(List<SysMenuEntity> list, SysMenuEntity t) {
        List<SysMenuEntity> tlist = new ArrayList<SysMenuEntity>();
        Iterator<SysMenuEntity> it = list.iterator();
        while (it.hasNext()) {
            SysMenuEntity n = (SysMenuEntity) it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 内链域名特殊字符替换
     *
     * @return
     */
    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, "."},
                new String[]{"", "", "", "/"});
    }
}

