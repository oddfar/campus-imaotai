package com.oddfar.campus.framework.aspectj;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.annotation.Log;
import com.oddfar.campus.common.domain.entity.SysOperLogEntity;
import com.oddfar.campus.common.domain.model.LoginUser;
import com.oddfar.campus.common.enums.BusinessStatus;
import com.oddfar.campus.common.filter.PropertyPreExcludeFilter;
import com.oddfar.campus.common.utils.SecurityUtils;
import com.oddfar.campus.common.utils.ServletUtils;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.common.utils.ip.IpUtils;
import com.oddfar.campus.framework.api.sysconfig.ConfigExpander;
import com.oddfar.campus.framework.manager.AsyncFactory;
import com.oddfar.campus.framework.manager.AsyncManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志记录处理
 *
 * @author 致远
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 排除敏感属性字段
     */
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};


    @Value("${spring.application.name:}")
    private String springApplicationName;


    /**
     * 切所有的controller包
     */
    @Pointcut("execution(* *..controller..*(..))")
    public void webLog() {

    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "webLog()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        boolean ensureMakeLog = this.ensureMakeLog(joinPoint);
        if (!ensureMakeLog) {
            return;
        }
        // 获取接口上@GetMapper等的name属性
        Map<String, Object> annotationProp = getAnnotationProp(joinPoint);

        handleLog(joinPoint, annotationProp, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(pointcut = "webLog()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        boolean ensureMakeLog = this.ensureMakeLog(joinPoint);
        if (!ensureMakeLog) {
            return;
        }
        // 获取接口上@GetMapper等的name属性
        Map<String, Object> annotationProp = getAnnotationProp(joinPoint);

        handleLog(joinPoint, annotationProp, e, null);
    }

    /**
     * AOP获取 @GetMapping等 的属性信息
     *
     * @param joinPoint joinPoint对象
     * @return 返回K, V格式的参数，key是参数名称，v是参数值
     */
    private Map<String, Object> getAnnotationProp(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        // 通过map封装参数和参数值，key参数名，value是参数值
        Map<String, Object> propMap = new HashMap<>(2);

        // 获取接口上的@GetMapping等的name属性 填充到map
        ApiResource apiResource = method.getDeclaringClass().getAnnotation(ApiResource.class);

        for (Annotation annotation : method.getAnnotations()) {
            //若是 spring 的请求注解
            if (annotation.toString().contains("Mapping(")) {
                // 填充其他属性
                String name = invokeAnnotationMethod(annotation, "name", String.class);
                propMap.put("log_content", StringUtils.isNull(name) ? "" : name);
            }
        }

        propMap.put("app_name", apiResource != null && StrUtil.isNotBlank(apiResource.appCode()) ? apiResource.appCode()
                : springApplicationName);

        /**
         * 以下是只填充 GetMapping 和 PostMapping
         */
//        GetMapping getResource = method.getAnnotation(GetMapping.class);
//        PostMapping postResource = method.getAnnotation(PostMapping.class);
//        if (getResource != null) {
//            propMap.put("log_content", getResource.name());
//        }
//
//        if (postResource != null) {
//            propMap.put("log_content", postResource.name());
//        }

        return propMap;
    }


    protected void handleLog(final JoinPoint joinPoint, Map<String, Object> annotationProp, final Exception e, Object jsonResult) {

        try {
            // *========数据库日志=========*//
            SysOperLogEntity operLog = new SysOperLogEntity();
            //设置appcode
            operLog.setAppName(annotationProp.get("app_name").toString());
            operLog.setLogName("API接口日志记录");
            operLog.setLogContent(annotationProp.get("log_content").toString());

            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 请求的地址
            String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            operLog.setOperIp(ip);
            operLog.setOperUrl(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255));

            if (SecurityUtils.isLogin()) {
                // 获取当前的用户
                LoginUser loginUser = SecurityUtils.getLoginUser();
                operLog.setOperUserId(loginUser.getUserId());
            }

            if (e != null) {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数 app name那些
            getControllerMethodDescription(joinPoint, operLog, jsonResult);
            operLog.setOperTime(new Date());
            // 保存数据库
            AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param operLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, SysOperLogEntity operLog, Object jsonResult) throws Exception {

        // 保存request，参数和值,获取参数的信息，传入到数据库中。
        setRequestValue(joinPoint, operLog);

        //保存response，参数和值
        if (StringUtils.isNotNull(jsonResult)) {
            operLog.setJsonResult(StringUtils.substring(JSON.toJSONString(jsonResult), 0, 2000));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLogEntity operLog) throws Exception {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) ServletUtils.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            operLog.setOperParam(StringUtils.substring(paramsMap.toString(), 0, 2000));
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (StringUtils.isNotNull(o) && !isFilterObject(o)) {
                    try {
                        String jsonObj = JSON.toJSONString(o, excludePropertyPreFilter());
                        params += jsonObj.toString() + " ";
                    } catch (Exception e) {
                    }
                }
            }
        }
        return params.trim();
    }

    /**
     * 确定当前接口是否需要记录日志
     * 参考：https://gitee.com/stylefeng/guns
     */
    private boolean ensureMakeLog(JoinPoint point) {
        // 判断是否需要记录日志，如果不需要直接返回
        Boolean openFlag = ConfigExpander.getGlobalControllerOpenFlag();

        // 获取类上的业务日志开关注解
        Class<?> controllerClass = point.getTarget().getClass();
        Log businessLog = controllerClass.getAnnotation(Log.class);

        // 获取方法上的业务日志开关注解
        Log methodBusinessLog = null;
        MethodSignature methodSignature = null;
        if (!(point.getSignature() instanceof MethodSignature)) {
            return false;
        }
        methodSignature = (MethodSignature) point.getSignature();
        Object target = point.getTarget();
        try {
            Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
            methodBusinessLog = currentMethod.getAnnotation(Log.class);
        } catch (NoSuchMethodException e) {
            return false;
        }

        // 如果开关开启
        if (openFlag) {
            // 如果控制器类上特意标明不做日志，则不记录日志
            if (businessLog != null && !businessLog.openLog()) {
                return false;
            }
            // 如果方法上标明不记录日志，则不记录日志
            return methodBusinessLog == null || methodBusinessLog.openLog();
        } else {
            // 如果全局开关没开启，但是类上有特殊标记开启日志，则以类上标注为准
            if (businessLog != null && businessLog.openLog()) {
                return true;
            }
            // 如果方法上标明不记录日志，则不记录日志
            return methodBusinessLog != null && methodBusinessLog.openLog();
        }

    }

    /**
     * 调用注解上的某个方法，并获取结果
     */
    private <T> T invokeAnnotationMethod(Annotation apiResource, String methodName, Class<T> resultType) {
        try {
            Class<? extends Annotation> annotationType = apiResource.annotationType();
            Method method = annotationType.getMethod(methodName);
            return (T) method.invoke(apiResource);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

        }
        return null;
    }

    /**
     * 忽略敏感属性
     */
    public PropertyPreExcludeFilter excludePropertyPreFilter() {
        return new PropertyPreExcludeFilter().addExcludes(EXCLUDE_PROPERTIES);
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

}
