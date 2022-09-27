package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理类
 *
 * @author my
 * @since 2022/9/24 15:41
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.info("ex => {}",exception.getMessage());
        String exceptionMessage = exception.getMessage();
        if(exceptionMessage.contains("Duplicate entry")){
            String[] split = exceptionMessage.split(" ");
            return R.error("用户名 "+split[2]+" 已存在");
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception){
        log.info("ex => {}",exception.getMessage());
        //我们可以在这里记录日志
        //往往我们是要记录到数据库中的
        return R.error(exception.getMessage());
    }
}