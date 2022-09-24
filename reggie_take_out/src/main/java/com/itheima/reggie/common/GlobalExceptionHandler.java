package com.itheima.reggie.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理类
 *
 * @author yjiiie6
 * @since 2022/9/24 15:17
 */
@ResponseBody // 将方法的返回值 R 对象转换为json格式的数据, 响应给页面
@ControllerAdvice(annotations = {RestController.class,Controller.class}) // 指定拦截那些类型的控制器
public class GlobalExceptionHandler {

    // 指定拦截的是哪一类型的异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> ExceptionHandler(SQLIntegrityConstraintViolationException SQLException) {

        // 解析异常的提示信息, 获取出是那个值违背了唯一约束
        if (SQLException.getMessage().contains("Duplicate entry")) {
            // 组装错误信息并返回
            String[] split = SQLException.getMessage().split(" ");
            String msg = split[2];
            return R.error("用户已存在 : " + msg);
        }

        return R.error("未知错误");
    }
}
