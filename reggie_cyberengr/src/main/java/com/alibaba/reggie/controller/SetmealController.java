package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.CustomException;
import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.dto.SetmealDto;
import com.alibaba.reggie.service.ISetmealService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * SetmealController
 *
 * @author cyberengr
 * @since 2022/9/29 9:39
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private ISetmealService setmealService;

    @PostMapping
    public Result<String> addSetmealDto(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return Result.success(GlobalConstant.FINISHED);
    }

    @GetMapping("/page")
    public Result<Page<SetmealDto>> pageResult(Long page, Long pageSize, String name) {
        if (page == null || pageSize == null) {
            throw new CustomException("传入参数有误！");
        }
        Page<SetmealDto> dtoPage = setmealService.getPage(page, pageSize, name);
        return Result.success(dtoPage);
    }
}
