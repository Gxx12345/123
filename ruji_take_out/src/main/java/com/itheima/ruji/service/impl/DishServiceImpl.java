package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.common.CustomException;
import com.itheima.ruji.dto.DishDto;
import com.itheima.ruji.entity.Dish;
import com.itheima.ruji.entity.DishFlavor;
import com.itheima.ruji.mapper.IDishMapper;
import com.itheima.ruji.service.IDishFlavorService;
import com.itheima.ruji.service.IDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.List;

/**
 * 菜品业务层实现类
 *
 * @author Gzz
 * @since 2022/9/26 16:54
 */

@Service
public class DishServiceImpl extends ServiceImpl<IDishMapper, Dish>implements IDishService {

@Autowired
 private IDishFlavorService iDishFlavorService;

    @Override
    @Transactional
    /**
     * 保存菜品及菜品口味信息
     *
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto) {
        // 1. 保存菜品信息
        Dish dish = new Dish();
        // 第一个参数是数据源
        // 第二个参数是要赋值的类
        // source -> target
        BeanUtils.copyProperties(dishDto, dish);
        save(dish);
        // 这个id就是保存之后的数据ID
        Long id = dish.getId();
        // 2. 菜品口味信息
        // 需要给口味信息中的dishId赋值.
        // 我们前端这个口味信息不是必须要传.
        // 校验前端是否添加了口味信息
        if (CollectionUtils.isNotEmpty(dishDto.getFlavors())) {
            for (DishFlavor flavor : dishDto.getFlavors()) {
                // 给子表中的主表ID赋值
                // 菜品ID
                flavor.setDishId(id);
            }
            iDishFlavorService.saveBatch(dishDto.getFlavors());
        }
        if (dishDto.getFlavors() != null || dishDto.getFlavors().size() > 0) {
//            for (DishFlavor flavor : dishDto.getFlavors()) {
//                // 给子表中的主表ID赋值
//                // 菜品ID
//                flavor.setDishId(dishId);
//            }
     //   this.dishFlavorService.saveBatch(dishDto.getFlavors());
        }
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
        List<DishFlavor> dishFlavorList = iDishFlavorService.list(dishFlavorWrapper);
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
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        this.updateById(dish);
        // 2.删除原来菜品的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 根据菜品id删除其口味信息
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        this.iDishFlavorService.remove(queryWrapper);
        // 3.把dishId重新赋值给flavors口味
        if (CollectionUtils.isNotEmpty(dishDto.getFlavors())) {
            for (DishFlavor item : dishDto.getFlavors()) {
                // 重新给口味信息赋值
                item.setDishId(dish.getId());
            }
        }
        // 4.添加新的口味
        this.iDishFlavorService.saveBatch(dishDto.getFlavors());
    }
}
