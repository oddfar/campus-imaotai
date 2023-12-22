package com.oddfar.campus.framework.listener;

import cn.hutool.core.util.StrUtil;
import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.constant.Constants;
import com.oddfar.campus.common.domain.entity.SysResourceEntity;
import com.oddfar.campus.common.utils.spring.AopTargetUtils;
import com.oddfar.campus.framework.api.resource.ResourceCollectorApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * api接口资源扫描
 * 参考 https://gitee.com/stylefeng/guns 项目
 *
 * @author oddfar
 */
public class ApiResourceScanner implements BeanPostProcessor {

    @Value("${spring.application.name:}")
    private String springApplicationName;


    /**
     * 权限资源收集接口
     */
    private final ResourceCollectorApi resourceCollectorApi;

    public ApiResourceScanner(ResourceCollectorApi resourceCollectorApi) {
        this.resourceCollectorApi = resourceCollectorApi;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object aopTarget = AopTargetUtils.getTarget(bean);

        if (aopTarget == null) {
            aopTarget = bean;
        }

        Class<?> clazz = aopTarget.getClass();
        // 判断是不是控制器,不是控制器就略过
        boolean controllerFlag = getControllerFlag(clazz);
        if (!controllerFlag) {
            return bean;
        }

        ApiResource classApiAnnotation = clazz.getAnnotation(ApiResource.class);
        if (classApiAnnotation != null) {
            // 扫描控制器的所有带ApiResource注解的方法
            List<SysResourceEntity> apiResources = doScan(clazz);
            // 将扫描到的注解转化为资源实体存储到缓存
            resourceCollectorApi.collectResources(apiResources);
        }

        return bean;
    }


    /**
     * 判断一个类是否是控制器
     */
    private boolean getControllerFlag(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (RestController.class.equals(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 扫描整个类中包含的所有@ApiResource资源
     */
    private List<SysResourceEntity> doScan(Class<?> clazz) {

        ArrayList<SysResourceEntity> apiResources = new ArrayList<>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        if (declaredMethods.length > 0) {
            for (Method declaredMethod : declaredMethods) {
                Annotation annotation = null;

                Annotation[] annotations = declaredMethod.getAnnotations();
                for (Annotation a : annotations) {
                    //若是 spring 的请求注解
                    if (a.toString().contains("Mapping(")) {
                        annotation = a;
                    }
                }

                if (annotation != null) {
                    SysResourceEntity definition = createDefinition(clazz, declaredMethod, annotation);
                    apiResources.add(definition);
                }
            }
        }
        return apiResources;
    }


    /**
     * 根据类信息，方法信息，注解信息创建 SysResourceEntity 对象
     */
    private SysResourceEntity createDefinition(Class<?> controllerClass, Method method, Annotation annotation) {
        SysResourceEntity resource = new SysResourceEntity();

        //设置类和方法名称
        resource.setClassName(controllerClass.getSimpleName());
        resource.setMethodName(method.getName());

        // 填充模块编码，模块编码就是控制器名称截取Controller关键字前边的字符串
        String className = resource.getClassName();

        int controllerIndex = className.indexOf("Controller");
        if (controllerIndex == -1) {
            throw new IllegalArgumentException("controller class name is not illegal, it should end with Controller!");
        }
        //去掉Controller
        String modular = className.substring(0, controllerIndex);
        resource.setModular_code(modular);

        // 填充模块的中文名称
        ApiResource apiResource = controllerClass.getAnnotation(ApiResource.class);
        // 接口资源的类别
        resource.setResourceBizType(apiResource.resBizType().getCode());
        resource.setModularName(apiResource.name());
        // 设置appCode
        if (StrUtil.isNotBlank(apiResource.appCode())) {
            resource.setAppCode(apiResource.appCode());
        }else {
            resource.setAppCode(springApplicationName);
        }
        //资源唯一编码
        String resourceCode = StrUtil.toUnderlineCase(resource.getAppCode()) + "." + StrUtil.toUnderlineCase(modular) + "." + StrUtil.toUnderlineCase(resource.getMethodName());
        resource.setResourceCode(resourceCode);

        //是否需要鉴权
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
        resource.setRequiredPermissionFlag(Constants.NO);
        if (preAuthorize != null) {
            if (preAuthorize.value().contains("@ss.resourceAuth")) {
                resource.setRequiredPermissionFlag(Constants.YES);
            }
        }

        // 填充其他属性
        String name = invokeAnnotationMethod(annotation, "name", String.class);
        //不存在则资源名称为方法名
        if (StringUtils.isNotEmpty(name)) {
            resource.setResourceName(name);
        } else {
            resource.setResourceName(resource.getMethodName());
        }

        String[] value = invokeAnnotationMethod(annotation, "value", String[].class);
        String controllerMethodPath = createControllerPath(controllerClass, value);
        resource.setUrl(controllerMethodPath);
        //填充请求方法
        resource.setHttpMethod(StringUtils.substringBetween(annotation.toString(), "annotation.", "Mapping").toLowerCase());

        return resource;
    }


    /**
     * 根据控制器类上的RequestMapping注解的映射路径，以及方法上的路径，拼出整个接口的路径
     */
    private String createControllerPath(Class<?> clazz, String[] paths) {
        String path = "";
        if (paths.length > 0) {
            path = "/" + paths[0];
        }
        String controllerPath;

        RequestMapping controllerRequestMapping = clazz.getDeclaredAnnotation(RequestMapping.class);
        if (controllerRequestMapping == null) {
            controllerPath = "";
        } else {
            String[] values = controllerRequestMapping.value();
            if (values.length > 0) {
                controllerPath = values[0];
            } else {
                controllerPath = "";
            }
        }

        // 控制器上的path要以/开头
        if (!controllerPath.startsWith("/")) {
            controllerPath = "/" + controllerPath;
        }

        // 前缀多个左斜杠替换为一个
        return (controllerPath + path).replaceAll("/+", "/");
    }


    /**
     * 调用注解上的某个方法，并获取结果
     *
     * @author fengshuonan
     * @date 2020/12/8 17:13
     */
    private <T> T invokeAnnotationMethod(Annotation apiResource, String methodName, Class<T> resultType) {
        try {
            Class<? extends Annotation> annotationType = apiResource.annotationType();
            Method method = annotationType.getMethod(methodName);
            return (T) method.invoke(apiResource);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

        }
        throw new RuntimeException("扫描api资源时出错!");
    }
}
