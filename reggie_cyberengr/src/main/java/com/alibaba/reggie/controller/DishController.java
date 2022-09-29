package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.entity.Category;
import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.dto.DishDto;
import com.alibaba.reggie.service.ICategoryService;
import com.alibaba.reggie.service.IDishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<DishDto>> pageResult(Long page, Long pageSize, String name) {
        if (page == null || pageSize == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        Page<Dish> dishPage = new Page<>();
        dishPage.setCurrent(page);
        dishPage.setSize(pageSize);
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name)
                .orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage, lambdaQueryWrapper);
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
     *
     * @param dishDtoParam
     * @return
     */
    @PostMapping
    public Result<String> insertDish(@RequestBody DishDto dishDtoParam) {
        if (dishDtoParam == null || dishDtoParam.getId() == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        dishService.saveWithFlavor(dishDtoParam);
        return Result.success(GlobalConstant.FINISHED);
    }

    /**
     * 根据id回显菜品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> getDishDto(@PathVariable Long id) {
        if (id == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        DishDto dto = dishService.getByIdWithFlavor(id);
        return Result.success(dto);
    }

    /**
     * 修改菜品信息
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> updateDish(@RequestBody DishDto dishDto) {
        if (dishDto == null || dishDto.getId() == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        dishService.updateWithFlavor(dishDto);
        return Result.success(GlobalConstant.FINISHED);
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> deleteByIds(@RequestParam List<Long> ids) {
        dishService.deleteByIds(ids);
        return Result.success(GlobalConstant.FINISHED);
    }

    /**
     * 批量修改菜品状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status, Long[] ids) {
        if (status == null || ids.length == 0) {
            return Result.error(GlobalConstant.FAILED);
        }
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId, ids)
                    .set(Dish::getStatus,status);
        boolean update = dishService.update(updateWrapper);
        return update ? Result.success(GlobalConstant.FINISHED) : Result.error(GlobalConstant.FAILED);

    }

    @GetMapping("/list")
    public Result<List<Dish>> getList(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(dish.getStatus()!=null,Dish::getStatus,dish.getStatus())
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return Result.success(list);
    }

}
