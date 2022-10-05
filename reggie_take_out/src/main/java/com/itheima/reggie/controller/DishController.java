package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.ICategoryService;
import com.itheima.reggie.service.IDishFlavorService;
import com.itheima.reggie.service.IDishService;
import com.itheima.reggie.service.ISetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品
 *
 * @author my
 * @since 2022/9/27 15:17
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private IDishService iDishService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IDishFlavorService dishFlavorService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        this.iDishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    /**
     * 菜品信息分页查询
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        //构建分页查询对象
        Page<Dish> queryPage = new Page<>();
        queryPage.setCurrent(page);
        queryPage.setSize(pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        Page<Dish> dishPage = this.iDishService.page(queryPage, queryWrapper);
        //构建返回返回结果对象，并cope查询结果到对象中
        Page<DishDto> result = new Page<>();
        BeanUtils.copyProperties(dishPage, result, "records");
        //遍历分页查询列表数据
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish item : dishPage.getRecords()) {
            DishDto dishDto = new DishDto();
            //dish -> dishDto
            BeanUtils.copyProperties(item, dishDto);
            //分类名称
            Category category = this.categoryService.getById(item.getCategoryId());
            if (category != null) {
                //分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            //把数据添加到数组中
            dishDtoList.add(dishDto);
        }
        //把Dish对象转为DishDto对象，同时赋值分类名称
        //封装数据并返回
        result.setRecords(dishDtoList);
        return R.success(result);
    }


    /**
     * 根据ID查询菜品信息
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = this.iDishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品数据
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("dishDto ==> {}", dishDto.toString());
        this.iDishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据分类ID查询菜品
     */
    @GetMapping("/list")
    //region 不查询口味的方法
    // public R<List<Dish>> getByCategoryId(Dish dish){
    //     Long categoryId = dish.getCategoryId();
    //     LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    //     queryWrapper.eq(Dish::getCategoryId,categoryId);
    //     queryWrapper.eq(Dish::getStatus,1);
    //     List<Dish> dishList = this.iDishService.list(queryWrapper);
    //     return R.success(dishList);
    // }
    //endregion
    //查询菜品方法修改
    public R<List<DishDto>> list(Dish dish) {
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = this.iDishService.list(queryWrapper);

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
        //        List<DishDto> dishDtoList = list.stream().map((item) -> {
//            DishDto dishDto = new DishDto();
//            BeanUtils.copyProperties(item,dishDto);
//            Long categoryId = item.getCategoryId();//分类id
//            //根据id查询分类对象
//            Category category = categoryService.getById(categoryId);
//            if(category != null){
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
//            //当前菜品的id
//            Long dishId = item.getId();
//            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
//            //SQL:select * from dish_flavor where dish_id = ?
//            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
//            dishDto.setFlavors(dishFlavorList);
//            return dishDto;
//        }).collect(Collectors.toList());
        //endregion
        return R.success(dishDtoList);
    }

    /**
     * 删除菜品
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids => {}", ids);
        this.iDishService.deleteWithFlavor(ids);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 起售/停售
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids ) {
        log.info("ids => {} , status => {}", ids, status);
        LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(CollectionUtils.isNotEmpty(ids),Dish::getId, ids)
                .set(Dish::getStatus, status);
        this.iDishService.update(wrapper);
        return R.success(GlobalConstant.FINISH);
    }
}
