package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.ICategoryService;
import com.itheima.reggie.service.ISetmealDishService;
import com.itheima.reggie.service.ISetmealService;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐控制层
 *
 * @author yjiiie6
 * @since 2022/9/29 11:10
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private ISetmealService iSetmealService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        iSetmealService.saveWithDish(setmealDto);

        return R.success(GlobalConstant.FINISH);
    }


    /**
     * 套餐分页查询
     *
     * @param page     当前页
     * @param pageSize 每页显示记录数
     * @param name     查询条件
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        // 组建分页对象
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);

        // 组建查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        iSetmealService.page(setmealPage, queryWrapper);

        // 定义返回结果
        Page<SetmealDto> result = new Page<>();
        // 转化结果为dto
        BeanUtils.copyProperties(setmealPage, result, "records");

        // 初始化dtoList
        // 查询分类数据
        List<SetmealDto> setmealDtoList = setmealPage.getRecords().stream().map(item -> {
            // 赋值
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            // 查询分类
            Category category = iCategoryService.getById(setmealDto.getCategoryId());
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        // 赋值
        result.setRecords(setmealDtoList);
        return R.success(result);
    }


    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(@RequestParam List<Long> ids) {
        iSetmealService.deleteByIds(ids);

        return R.success(GlobalConstant.FINISH);
    }
}
