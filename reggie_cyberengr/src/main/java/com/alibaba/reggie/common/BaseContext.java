package com.alibaba.reggie.common;

/**
 * 基于ThreadLocal封装的工具类
 *
 * @author cyberengr
 * @since 2022/9/26 10:51
 */
public class BaseContext {
    private static final ThreadLocal THREAD_LOCAL_CURRENT_ID = new ThreadLocal();

    public static Long getSetThreadLocalCurrentId() {
        return (Long) THREAD_LOCAL_CURRENT_ID.get();
    }

    public static void setSetThreadLocalCurrentId(Long id) {
       THREAD_LOCAL_CURRENT_ID.set(id);
    }
}
