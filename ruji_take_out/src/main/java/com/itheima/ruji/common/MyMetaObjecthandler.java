
package com.itheima.ruji.common;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 自定义数据对象处理器
 *
 * @author Gzz
 @since 2022/9/25 14:36
 */
@Slf4j
@Component
public class MyMetaObjecthandler implements MetaObjectHandler {

/**
     * 插入操作，自动填充
     * @param metaObject
     */

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info("自动填充当前线程的id:{}",Thread.currentThread().getName());
        log.info(metaObject.toString());
        LocalDateTime now = LocalDateTime.now();
        //创建时间
        metaObject.setValue("createTime", now);
        //修改时间
        metaObject.setValue("updateTime",now);
        //在请求,拿到当前登录用户的id,然后传递到自动填充的这个方法中
        metaObject.setValue("createUser",BaseTreadlock.getCurrentId());
        metaObject.setValue("updateUser",BaseTreadlock.getCurrentId());
    }

/**
     * 更新操作，自动填充
     * @param metaObject
     */

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());

        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseTreadlock.getCurrentId());
    }
}

