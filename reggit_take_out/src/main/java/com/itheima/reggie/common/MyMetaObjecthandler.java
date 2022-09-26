package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * 公共字段
 *
 * @author XR
 * @since 2022/9/26 10:57
 */
@Slf4j
@Component
public class MyMetaObjecthandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject){
        log.info("公共字段自动填充中:当前线程ID ===> {}",Thread.currentThread().getId());
        LocalDateTime now=LocalDateTime.now();
        metaObject.setValue("createTime",now);
        metaObject.setValue("updateTime",now);
        metaObject.setValue("createUser", BaseContext.getCurrentUserId());
        metaObject.setValue("updateUser", BaseContext.getCurrentUserId());
    }
    @Override
    public void updateFill(MetaObject metaObject){
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUse",1L);
    }
}
