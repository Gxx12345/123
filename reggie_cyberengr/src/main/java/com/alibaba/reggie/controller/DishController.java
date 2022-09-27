package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.CustomException;
import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.entity.Category;
import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.entity.DishDto;
import com.alibaba.reggie.service.ICategoryService;
import com.alibaba.reggie.service.IDishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<DishDto>> pageResult(Long page, Long pageSize, String name) {
        Page<Dish> dishPage = new Page<>();
        dishPage.setCurrent(page);
        dishPage.setSize(pageSize);
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotBlank(name),Dish::getName,name)
                .orderByDesc(Dish::getUpdateTime);
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

    /**
     * 新增菜品
     * @param dishDtoParam
     * @return
     */
    @PostMapping
    public Result<String> insertDish(@RequestBody DishDto dishDtoParam) {
        dishService.saveWithFlavor(dishDtoParam);
        return Result.success(GlobalConstant.FINISHED);
    }

    /**
     * 根据id回显菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> getDishDto(@PathVariable Long id) {
        DishDto dto = dishService.getByIdWithFlavor(id);
        return Result.success(dto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> updateDish(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return Result.success(GlobalConstant.FINISHED);
    }
}
