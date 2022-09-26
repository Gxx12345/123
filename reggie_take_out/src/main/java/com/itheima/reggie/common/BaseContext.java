package com.itheima.reggie.common;

/**
 * ThreadLocal包装类
 *
 * @author yjiiie6
 * @since 2022/9/26 11:06
 */
public class BaseContext {
    private static final ThreadLocal<Long> THREAD_LOCAL_CURRENT_USER = new ThreadLocal<>();

    /**
     * 设置当前登录用户的id
     * @param currentUserId 过滤器调用方法传来的登录用户的id
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
        // 获取我们写入的值
        return THREAD_LOCAL_CURRENT_USER.get();
    }
}
