package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.entity.Category;
import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.dto.DishDto;
import com.alibaba.reggie.entity.DishFlavor;
import com.alibaba.reggie.service.ICategoryService;
import com.alibaba.reggie.service.IDishFlavorService;
import com.alibaba.reggie.service.IDishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @Autowired
    private IDishFlavorService dishFlavorService;

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

    //region 未完善的显示列表
    /*@GetMapping("/list")
    public Result<List<Dish>> getList(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(dish.getStatus()!=null,Dish::getStatus,dish.getStatus())
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return Result.success(list);
    }*/
    //endregion

    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish) {
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        //region foreach写法
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish item : list) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            dishDtoList.add(dishDto);
        }
        //endregion
        //region Lambda使用Map方法的写法
        /*  List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());*/
        //endregion
        return Result.success(dishDtoList);
    }

}
