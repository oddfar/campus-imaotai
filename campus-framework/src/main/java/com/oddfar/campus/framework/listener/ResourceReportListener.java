package com.oddfar.campus.framework.listener;

import com.oddfar.campus.common.domain.entity.SysResourceEntity;
import com.oddfar.campus.framework.service.SysResourceService;
//import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

/**
 * 监听项目初始化完毕，导入资源信息(没用，暂存下代码)
 */
//@Component  implements ApplicationListener<ApplicationReadyEvent>
public class ResourceReportListener  {


    @Autowired
    WebApplicationContext applicationContext;
    @Autowired
    SysResourceService resourceService;

//    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //清空表
        resourceService.truncateResource();

        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 获取所有controller方法
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        map.keySet().forEach(info -> {
            HandlerMethod handlerMethod = map.get(info);
            PreAuthorize preAuthorize = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), PreAuthorize.class);
            //如果方法上含有  @PreAuthorize 注解
            Optional.ofNullable(preAuthorize).ifPresent(anonymous -> {
                if (preAuthorize.value().contains("@ss.test")) {
                    Method m = handlerMethod.getMethod();
                    setSource(m);
                }
            });
        });

    }


    /**
     * 根据 Method 设置其内容
     *
     * @param m
     */
    private void setSource(Method m) {

        Class<?> aClass = m.getDeclaringClass();
        Annotation[] annotations = m.getDeclaredAnnotations();

        for (Annotation a : annotations) {
            //若是 spring 的请求注解
            if (a.toString().contains("Mapping(")) {
                SysResourceEntity resource = new SysResourceEntity();

                resource.setClassName(aClass.getSimpleName());
                //获取注解@Api，并设置模块名称
//                Api api = AnnotationUtils.getAnnotation(aClass, Api.class);
//                resource.setModularName(api.value());

                resource.setMethodName(m.getName());
                //设置注解其他内容
                setSource(resource, a);
                //保存到数据库
                resourceService.insertResource(resource);
            }
        }

    }

    /**
     * 设置 SysResourceEntity 的其他内容
     *
     * @param resource
     * @param a        例如 @GetMapping，设置其参数的内容
     */
    private void setSource(SysResourceEntity resource, Annotation a) {
        String s = a.toString();
        resource.setResourceName(StringUtils.substringBetween(s, ", name=", ", "));
        resource.setUrl(StringUtils.substringBetween(s, ", value=", ", "));
        resource.setHttpMethod(StringUtils.substringBetween(s, "annotation.", "Mapping"));

        resource.setAppCode("zhiyuan");
        //ClassName+MethodName(替换)
        resource.setResourceCode(resource.getClassName().toLowerCase().replace("controller", "") + ":" + resource.getMethodName());

    }

}
