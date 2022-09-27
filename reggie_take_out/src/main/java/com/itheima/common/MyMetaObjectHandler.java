package com.itheima.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * mp公共字段的填充
 *
 * @author L
 * @since 2022/9/26 10:43
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入原对象字段填充(用于插入时对公共字段的填充)
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充中:当前线程ID === > {}",Thread.currentThread().getId());
        //给createTime字段赋值
        LocalDateTime now = LocalDateTime.now();
        //创建时间
        metaObject.setValue("createTime",now);
        //修改时间
        metaObject.setValue("updateTime",now);
        //创建人
        //在请求是，当拿到当前登录用户的ID，然后传递到自动填充的这个方法中
        metaObject.setValue("createUser",BaseContext.getCurrentUserId());
        //修改人
        metaObject.setValue("updateUser",BaseContext.getCurrentUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //修改时间
        metaObject.setValue("updateTime",LocalDateTime.now());
        //修改人
        metaObject.setValue("updateUser",BaseContext.getCurrentUserId());
    }
}
