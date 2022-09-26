package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公共字段管理类
 *
 * @author yjiiie6
 * @since 2022/9/26 10:46
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        // 设置创建时间
        metaObject.setValue("createTime",now);
        // 设置修改时间
        metaObject.setValue("updateTime",now);
        // 设置创建人
        metaObject.setValue("createUser",BaseContext.getCurrentUserId());
        // 设置修改人
        metaObject.setValue("updateUser",BaseContext.getCurrentUserId());

    }

    @Override
    public void updateFill(MetaObject metaObject) {

        // 设置修改时间
        metaObject.setValue("updateTime",LocalDateTime.now());

        // 设置修改人
        metaObject.setValue("updateUser",BaseContext.getCurrentUserId());
    }
}
