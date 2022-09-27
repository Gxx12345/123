package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.CustomException;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.entity.Category;
import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.entity.DishDto;
import com.alibaba.reggie.service.ICategoryService;
import com.alibaba.reggie.service.IDishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品
 *
 * @author cyberengr
 * @since 2022/9/26 20:19
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private IDishService dishService;
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/page")
    public Result<Page<DishDto>> pageResult(Long page, Long pageSize, String name) {
        Page<Dish> dishPage = new Page<>();
        dishPage.setCurrent(page);
        dishPage.setSize(pageSize);
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Dish::getName,name)
                .orderByAsc(Dish::getUpdateTime);
        dishService.page(dishPage,lambdaQueryWrapper);
        if (dishPage.getRecords() == null) {
            return Result.success(new Page<DishDto>());
        }
        Page<DishDto> result = new Page<>();
        BeanUtils.copyProperties(dishPage, result, "records");
        List<DishDto> collect = dishPage.getRecords().stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        result.setRecords(collect);
        return Result.success(result);
    }
}
