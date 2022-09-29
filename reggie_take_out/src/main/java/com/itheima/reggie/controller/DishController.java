package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.ICategoryService;
import com.itheima.reggie.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品控制层
 *
 * @author yjiiie6
 * @since 2022/9/27 14:09
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private IDishService iDishService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 新增菜品
     *
     * @param dishDtoParam
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDtoParam) {

        iDishService.saveWithFlavor(dishDtoParam);

        return R.success(GlobalConstant.FINISH);
    }


    /**
     * 菜品分页查询
     *
     * @param page     当前页
     * @param pageSize 当前页显示记录数
     * @param name     查询条件
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        // 构造分页条件对象  并设置当前页及当前页显示记录数
        Page<Dish> queryPage = new Page<>(page, pageSize);

        // 构建查询及排序条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 模糊匹配  -- 校验name
        queryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);
        // 方便用户使用，更新时间倒序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页条件查询
        Page<Dish> dishPage = iDishService.page(queryPage, queryWrapper);

        // 构建返回结果对象 ， 并copy查询结构到该对象中
        Page<DishDto> result = new Page<>();
        // 忽略掉records这个属性，不做赋值的操作
        BeanUtils.copyProperties(dishPage, result, "records");

        //region lambda表达式map写法
        // 使用lambda表达式map的写法
        /*List<DishDto> dishDtoList = dishPage.getRecords().stream().map(item -> {
            DishDto dishDto = new DishDto();
            // Dish -> DishDto
            BeanUtils.copyProperties(item, dishDto);
            // 分类名称
            Category category = iCategoryService.getById(item.getCategoryId());
            if (category != null) {
                // 分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;

        }).collect(Collectors.toList());*/
        //endregion

        //region 使用lambda表达式的foreach实现
        //        List<DishDto> dishDtoList = new ArrayList<>();
//        dishPage.getRecords().forEach(item -> {
//            DishDto dishDto = new DishDto();
//            // Dish -> DishDto
//            BeanUtils.copyProperties(item, dishDto);
//            // 分类名称
//            Category category = this.categoryService.getById(item.getCategoryId());
//            if (category != null) {
//                // 分类名称赋值
//                dishDto.setCategoryName(category.getName());
//                dishDtoList.add(dishDto);
//            }
//        });
        //endregion

        //region for循环
        // 遍历分页查询列表数据
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish item : dishPage.getRecords()) {
            DishDto dishDto = new DishDto();
            // Dish —> DishDto
            BeanUtils.copyProperties(item, dishDto);
            // 分类名称
            Category category = iCategoryService.getById(item.getCategoryId());
            if (category != null) {
                // 分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            // 把数据添加到集合中
            dishDtoList.add(dishDto);
        }
        //endregion

        // 把Dish对象转为DishDto对象，同时赋值分类名称
        // 封装数据并返回
        result.setRecords(dishDtoList);
        return R.success(result);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     *
     * @param id
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = iDishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 更新菜品信息，同时更新对应的口味信息
     *
     * @param dishDtoParam
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDtoParam) {

        iDishService.updateWithFlavor(dishDtoParam);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        Long categoryId = dish.getCategoryId();

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null , Dish::getCategoryId, categoryId);
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        List<Dish> dishList = iDishService.list(queryWrapper);

        return R.success(dishList);
    }
}
