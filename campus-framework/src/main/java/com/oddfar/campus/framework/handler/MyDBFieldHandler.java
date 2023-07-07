package com.oddfar.campus.framework.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.oddfar.campus.common.domain.BaseEntity;
import com.oddfar.campus.common.utils.ServletUtils;
import com.oddfar.campus.common.utils.web.WebFrameworkUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;
import java.util.Objects;

/**
 * mybatis-plus 通用参数填充实现类
 */
public class MyDBFieldHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
//        this.strictInsertFill(metaObject, "createTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)

        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) metaObject.getOriginalObject();

            Date current = new Date();
            // 创建时间为空，则以当前时间为插入时间
            if (Objects.isNull(baseEntity.getCreateTime())) {
                baseEntity.setCreateTime(current);
            }
            // 更新时间为空，则以当前时间为更新时间
            if (Objects.isNull(baseEntity.getUpdateTime())) {
                baseEntity.setUpdateTime(current);
            }
            //TODO getRequest2
            Long userId = WebFrameworkUtils.getLoginUserId(ServletUtils.getRequest());

            // 当前登录用户不为空，创建人为空，则当前登录用户为创建人
            if (Objects.nonNull(userId) && Objects.isNull(baseEntity.getCreateUser())) {
                baseEntity.setCreateUser(userId);
            }
            // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
            if (Objects.nonNull(userId) && Objects.isNull(baseEntity.getUpdateUser())) {
                baseEntity.setUpdateUser(userId);
            }
            if (Objects.isNull(baseEntity.getDelFlag())) {
                baseEntity.setDelFlag(0);
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
//        this.strictUpdateFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)

        // 更新时间为空，则以当前时间为更新时间
        Object modifyTime = getFieldValByName("updateTime", metaObject);
        if (Objects.isNull(modifyTime)) {
            setFieldValByName("updateTime", new Date(), metaObject);
        }

        // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
        Object modifier = getFieldValByName("updateUser", metaObject);
        Long userId = WebFrameworkUtils.getLoginUserId(ServletUtils.getRequest());
        if (Objects.nonNull(userId) && Objects.isNull(modifier)) {
            setFieldValByName("updateUser", userId, metaObject);
        }
    }
}
