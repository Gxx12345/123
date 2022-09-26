package com.alibaba.reggie.common;

import com.alibaba.reggie.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 *
 * @author cyberengr
 * @since 2022/9/24 15:17
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    /**
     * 添加新员工是时返回用户已存在异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<String> exception(Exception e) {
        log.error("e==>{}",e.getMessage());
        if(e instanceof SQLIntegrityConstraintViolationException) {
            if (e.getMessage().contains("Duplicate entry")) {
                String[] split = e.getMessage().split(" ");
                return Result.error("用户"+split[2]+"已存在");
            }
        }
        if(e instanceof CustomException) {
            return Result.error(e.getMessage());
        }
        return Result.error("未知错误!");
    }
}
