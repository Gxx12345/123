package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * mp的公共字段自动填充
 *
 * @author Gmy
 * @since 2022/9/26 11:03
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());

        //  给createTime字段赋值
        LocalDateTime now = LocalDateTime.now();
        // 创建时间
        metaObject.setValue("createTime", now);
        // 修改时间
        metaObject.setValue("updateTime",now);
        // 创建人
        // 在请求时,拿到当前登录用户的ID,然后传递到自动填充的这个方法中
        metaObject.setValue("createUser", BaseContext.getCurrentUserId());
        // 修改人
        metaObject.setValue("updateUser",BaseContext.getCurrentUserId());
    }

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());
        // 修改时间
        metaObject.setValue("updateTime",LocalDateTime.now());
        // 修改人
        metaObject.setValue("updateUser",1L);
    }
}
