package com.itheima.ruji.common;

/**
 * 基于ThreadLock封装的工具类,用于保存和获取信息ID
 *
 * @author Gzz
 * @since 2022/9/26 10:43
 */


public class BaseTreadlock {
    private static final ThreadLocal<Long> THREAD_LOCAL_CURRENT_USER= new ThreadLocal<>();

    /**
     * 设置值id
     */
    public static void setCurrentId(Long id){
        THREAD_LOCAL_CURRENT_USER.set(id);
    }
    public static Long getCurrentId(){
        return THREAD_LOCAL_CURRENT_USER.get();
    }
}
