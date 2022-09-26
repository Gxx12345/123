package com.itheima.reggie.common;

/**
 * 自定义业务异常类
 *
 * @author Gmy
 * @since 2022/9/26 19:25
 */
public class CustomException extends RuntimeException{
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public CustomException(String message){
        super(message);
    }
}
