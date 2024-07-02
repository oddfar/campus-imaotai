package com.oddfar.campus.framework.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.oddfar.campus.framework.handler.DateTypeHandler;
import com.oddfar.campus.framework.handler.MyDBFieldHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("${mybatis-plus.mapperPackage}")
public class MybatisPlusConfig {

    // driverClassName如果是SQLite，需要对时间转换
    @Value("${spring.datasource.dynamic.datasource.master.driverClassName}")
    private String driverClassName;

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }


    /**
     * 自动填充参数类
     *
     * @return
     */
    @Bean
    public MetaObjectHandler defaultMetaObjectHandler() {
        return new MyDBFieldHandler();
    }

    @Bean
    public DateTypeHandler defaultDateTypeHandler() {
        if (driverClassName.contains("sqlite")) {
            return new DateTypeHandler();
        }
        return null;
    }


}
