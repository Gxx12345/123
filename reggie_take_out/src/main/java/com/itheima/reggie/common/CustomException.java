package com.itheima.reggie.common;

/**
 * 自定义异常
 *
 * @author my
 * @since 2022/9/26 17:43
 */
public class CustomException extends RuntimeException{

    public CustomException(String message) {
        super(message);
    }
}
