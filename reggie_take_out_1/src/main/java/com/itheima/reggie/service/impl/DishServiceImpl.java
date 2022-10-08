package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.IDishFlavorService;
import com.itheima.reggie.service.IDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜品的业务层实现类
 *
 * @author t3rik
 * @since 2022/9/26 16:40
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    @Autowired
    private IDishFlavorService dishFlavorService;

    /**
     * 保存菜品及菜品口味信息
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 1. 保存菜品信息
        Dish dish = new Dish();
        // dishDto -> dish
        // 第一个参数是数据源
        // 第二个参数是要赋值的类
        // source -> target
        BeanUtils.copyProperties(dishDto, dish);
        this.save(dish);
        // 这个id就是保存之后的数据ID
        Long dishId = dish.getId();
        // 2. 菜品口味信息
        // 需要给口味信息中的dishId赋值.
        // 我们前端这个口味信息不是必须要传.
        // 校验前端是否添加了口味信息
        if (CollectionUtils.isNotEmpty(dishDto.getFlavors())) {
            for (DishFlavor flavor : dishDto.getFlavors()) {
                // 给子表中的主表ID赋值
                // 菜品ID
                flavor.setDishId(dishId);
            }
            this.dishFlavorService.saveBatch(dishDto.getFlavors());
        }
//        if (dishDto.getFlavors() != null || dishDto.getFlavors().size() > 0) {
//            for (DishFlavor flavor : dishDto.getFlavors()) {
//                // 给子表中的主表ID赋值
//                // 菜品ID
//                flavor.setDishId(dishId);
//            }
//            this.dishFlavorService.saveBatch(dishDto.getFlavors());
//        }
    }

    /**
     * 根据id查询菜品及口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 1 查询菜品
        Dish dish = this.getById(id);
        if (dish == null) {
            throw new CustomException("传入的参数有误");
        }
        // 2 构建dishDto,赋值
        DishDto dishDto = new DishDto();
        // 第一个参数,是有值的
        // 第二个参数,是要赋值给其的目标 target
        BeanUtils.copyProperties(dish, dishDto);
        // 3 根据这个菜品查询其口味
        // 需要口味service
        LambdaQueryWrapper<DishFlavor> dishFlavorWrapper = new LambdaQueryWrapper<>();
        // 根据菜品ID(dishId)查询这个菜品的口味
        dishFlavorWrapper.eq(DishFlavor::getDishId, dish.getId());
        // 口味信息
        List<DishFlavor> dishFlavorList = this.dishFlavorService.list(dishFlavorWrapper);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    /**
     * 更新菜品信息及其口味信息
     *
     * @param dishDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithFlavor(DishDto dishDto) {
        // 1.先更新Dish表
        // dishDto 这里是菜品信息
        Dish dish = new Dish();
        // 赋值的操作
        // dishDto -> dish
        BeanUtils.copyProperties(dishDto, dish);
        // 更新菜品表
        this.updateById(dish);
        // 菜品id
        Long dishId = dish.getId();
        // 2.删除原来菜品的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 根据菜品id删除其口味信息
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        // delete from dish_flavor where dish_id = 1397849739276890114;
        // 前端显示的口味信息,是可以更改
        // 初次返回的口味信息的值 {"id" : "1111","name": "asdasd","value":"asdasd"}
        // 修改之后 {"id" : "","name": "asdasd","value":"asdasd"}
        this.dishFlavorService.remove(queryWrapper);

        // 3.把dishId重新赋值给flavors口味
        if (CollectionUtils.isNotEmpty(dishDto.getFlavors())) {
            for (DishFlavor item : dishDto.getFlavors()) {
                // 重新给口味信息赋值
                item.setDishId(dish.getId());
            }
        }
        // 4.添加新的口味
        this.dishFlavorService.saveBatch(dishDto.getFlavors());
    }
}
