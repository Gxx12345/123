package com.itheima.reggie.common;

import com.itheima.reggie.common.pojo.UserInfo;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 *
 * @author Gmy
 * @since 2022/9/26 11:18
 */
public class BaseContext {
    private static final ThreadLocal<Long> THREAD_LOCAL_CURRENT_USER = new ThreadLocal<>();
    /**
     * 设置当前登录用户ID
     * @param currentUserId
     */
    public static void setCurrentUserId(Long currentUserId) {
        THREAD_LOCAL_CURRENT_USER.set(currentUserId);
    }

    /**
     * 获取当前登录用户ID
     * @return
     */
    public static Long getCurrentUserId() {
        return THREAD_LOCAL_CURRENT_USER.get();
    }
}
