package com.alibaba.reggie.exception;

/**
 * 删除分类异常
 *
 * @author cyberengr
 * @since 2022/9/26 17:32
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
