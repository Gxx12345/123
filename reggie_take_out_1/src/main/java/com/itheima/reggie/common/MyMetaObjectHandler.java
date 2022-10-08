package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * mp的公共字段自动填充
 *
 * @author t3rik
 * @since 2022/9/26 09:44
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入元对象字段填充（用于插入时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
//        log.info("公共字段填充中:当前线程ID ===> {}", Thread.currentThread().getId());
        // 给createTime字段赋值
        LocalDateTime now = LocalDateTime.now();
        // 创建时间
        metaObject.setValue("createTime", now);
        // 修改时间
        metaObject.setValue("updateTime", now);
        // 创建人
        // admin5 -> 1123123123
        // 在请求时,拿到当前登录用户的ID,然后传递到自动填充的这个方法中
        metaObject.setValue("createUser", BaseContext.getCurrentUserId());
        // 修改人
        metaObject.setValue("updateUser", BaseContext.getCurrentUserId());
    }

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 修改时间
        metaObject.setValue("updateTime", LocalDateTime.now());
        // 修改人
        metaObject.setValue("updateUser", 1L);
    }
}
