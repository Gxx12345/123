package com.itheima.reggie.common;

/**
 * 当前用户信息
 *
 * @author t3rik
 * @since 2022/9/26 10:34
 */
public class BaseContext {
    // ThreadLocal
    private static final ThreadLocal<Long> THREAD_LOCAL_CURRENT_USER = new ThreadLocal<>();

    /**
     * 设置当前登录用户ID
     * @param currentUserId
     */
    public static void setCurrentUserId(Long currentUserId) {
        // 把值写入到了ThreadLocal中
        THREAD_LOCAL_CURRENT_USER.set(currentUserId);
    }

    /**
     * 获取当前登录用户ID
     * @return
     */
    public static Long getCurrentUserId() {
        //  获取我们写入的值
        return THREAD_LOCAL_CURRENT_USER.get();
    }
}
