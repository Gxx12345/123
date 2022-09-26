package com.itheima.ruji.common;

/**
 * 自定义异常类
 *
 * @author Gzz
 * @since 2022/9/26 17:52
 */


public class CustomException extends RuntimeException{

    public CustomException(String message) {
        super(message);
    }
}
