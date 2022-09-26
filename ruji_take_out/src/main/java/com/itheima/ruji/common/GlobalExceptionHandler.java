package com.itheima.ruji.common;

import com.itheima.ruji.controller.EmployeeController;
import com.itheima.ruji.filter.LoginCheckFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLClientInfoException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 *
 * @author Gzz
 * @since 2022/9/24 15:14
 */
//这个表示捕获异常得范围 (这个范围不写也能捕获)
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        //输入错的错误 前端报出: Duplicate entry 'v5587' for key 'employee.id'
        if(ex.getMessage().contains("Duplicate entry")){
            //字符串切割
            String[] s = ex.getMessage().split(" ");
            return R.error(s[2]+"存在");
        }
        return R.error("未知错误");
    }
    @ExceptionHandler(CustomException.class)
    public R<String>exceptionHandler(CustomException exception){
        log.info("ex----->{}",exception.toString());
        //我们可以再这里记录日志
        //往往我们是要记录在数据库中
        return  R.error(exception.getMessage());
    }
}
