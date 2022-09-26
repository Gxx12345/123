package com.alibaba.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公共字段填充
 *
 * @author cyberengr
 * @since 2022/9/26 10:43
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 设置公共字段createTime updateTime createUser updateUser
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        metaObject.setValue("createTime",now);
        metaObject.setValue("updateTime",now);
        metaObject.setValue("createUser",BaseContext.getSetThreadLocalCurrentId());
        metaObject.setValue("updateUser",BaseContext.getSetThreadLocalCurrentId());
    }

    /**
     * 设置公共字段createUser updateUser
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("createUser",BaseContext.getSetThreadLocalCurrentId());
        metaObject.setValue("updateUser",BaseContext.getSetThreadLocalCurrentId());
    }
}
