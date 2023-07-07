package com.oddfar.campus.framework.web.service;

import cn.hutool.core.util.StrUtil;
import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.domain.entity.SysRoleEntity;
import com.oddfar.campus.common.domain.model.LoginUser;
import com.oddfar.campus.common.utils.SecurityUtils;
import com.oddfar.campus.common.utils.ServletUtils;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.security.context.PermissionContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 自定义权限实现，及自定义controller接口资源权限，ss取自SpringSecurity首字母
 * （根据若依修改）
 *
 * @author zhiyuan
 */
@Service("ss")
public class PermissionService {
    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

    @Value("${spring.application.name:}")
    private String springApplicationName;

    @Autowired
    WebApplicationContext applicationContext;

    /**
     * 所有权限标识
     */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 管理员角色权限标识
     */
    private static final String SUPER_ADMIN = "admin";

    private static final String ROLE_DELIMETER = ",";

    private static final String PERMISSION_DELIMETER = ",";

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermi(String permission) {
        if (StringUtils.isEmpty(permission)) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions())) {
            return false;
        }
        PermissionContextHolder.setContext(permission);
        return hasPermissions(loginUser.getPermissions(), permission);
    }

    /**
     * 验证用户是否具备某接口
     * @return 用户是否具备某接口
     */
    public boolean resourceAuth() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser)) {
            return false;
        }

        HttpServletRequest request = ServletUtils.getRequest();
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        HandlerExecutionChain handlerChain = null;
        try {
            handlerChain = mapping.getHandler(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //通过处理链找到对应的HandlerMethod类
        HandlerMethod handler = (HandlerMethod) handlerChain.getHandler();
        String resourceCode = getResourceCode(handler);

        return hasResources(loginUser.getResources(), resourceCode);
    }


    /**
     * 获取controller方法的编码
     *
     * @param handler
     * @return
     */
    private String getResourceCode(HandlerMethod handler) {
        Object bean = handler.getBean();//处理请求的类
        Class<?> aClass = bean.getClass();
        Method method = handler.getMethod();//处理请求的方法
        //获取@ApiResource接口
        ApiResource apiResource =  method.getDeclaringClass().getAnnotation(ApiResource.class);
        //资源唯一编码
        StringBuffer resourceCode = new StringBuffer();
        //应用编码
        String appCode = springApplicationName;
        if (apiResource != null) {
            if (StringUtils.isNotEmpty(apiResource.appCode())) {
                appCode = apiResource.appCode();
            }
        }
        String className = StrUtil.toUnderlineCase(StringUtils.substringBefore(aClass.getSimpleName(), "$$")
                .replace("Controller", ""));

        String methodName = StrUtil.toUnderlineCase(method.getName());

        return resourceCode.append(appCode).append(".").append(className).append(".").append(methodName).toString();

    }

    /**
     * 判断是否包含接口
     *
     * @param resources 资源列表
     * @param resource  资源接口字符串
     * @return 用户是否具备某权限
     */
    private boolean hasResources(Set<String> resources, String resource) {
        return resources.contains(ALL_PERMISSION) || resources.contains(StringUtils.trim(resource));
    }

    /**
     * 验证用户是否不具备某权限，与 hasPermi逻辑相反
     *
     * @param permission 权限字符串
     * @return 用户是否不具备某权限
     */
    public boolean lacksPermi(String permission) {
        return hasPermi(permission) != true;
    }

    /**
     * 验证用户是否具有以下任意一个权限
     *
     * @param permissions 以 PERMISSION_NAMES_DELIMETER 为分隔符的权限列表
     * @return 用户是否具有以下任意一个权限
     */
    public boolean hasAnyPermi(String permissions) {
        if (StringUtils.isEmpty(permissions)) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions())) {
            return false;
        }
        PermissionContextHolder.setContext(permissions);
        Set<String> authorities = loginUser.getPermissions();
        for (String permission : permissions.split(PERMISSION_DELIMETER)) {
            if (permission != null && hasPermissions(authorities, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否拥有某个角色
     *
     * @param role 角色字符串
     * @return 用户是否具备某角色
     */
    public boolean hasRole(String role) {
        if (StringUtils.isEmpty(role)) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getUser().getRoles())) {
            return false;
        }
        for (SysRoleEntity sysRole : loginUser.getUser().getRoles()) {
            String roleKey = sysRole.getRoleKey();
            if (SUPER_ADMIN.equals(roleKey) || roleKey.equals(StringUtils.trim(role))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证用户是否不具备某角色，与 isRole逻辑相反。
     *
     * @param role 角色名称
     * @return 用户是否不具备某角色
     */
    public boolean lacksRole(String role) {
        return hasRole(role) != true;
    }

    /**
     * 验证用户是否具有以下任意一个角色
     *
     * @param roles 以 ROLE_NAMES_DELIMETER 为分隔符的角色列表
     * @return 用户是否具有以下任意一个角色
     */
    public boolean hasAnyRoles(String roles) {
        if (StringUtils.isEmpty(roles)) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getUser().getRoles())) {
            return false;
        }
        for (String role : roles.split(ROLE_DELIMETER)) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否包含权限
     *
     * @param permissions 权限列表
     * @param permission  权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(Set<String> permissions, String permission) {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(StringUtils.trim(permission));
    }
}
