package com.itheima.reggie.common;

/**
 * ThreadLocal包装类
 *
 * @author XR
 * @since 2022/9/26 11:11
 */
public class BaseContext {
    private static final ThreadLocal<Long> THREAD_LOCAL_CURRENT_USER=new ThreadLocal<>();
    public static void setCurrentUserId(Long currentUserId){
        THREAD_LOCAL_CURRENT_USER.set(currentUserId);
    }
    public static Long getCurrentUserId(){
        return THREAD_LOCAL_CURRENT_USER.get();
    }
}
