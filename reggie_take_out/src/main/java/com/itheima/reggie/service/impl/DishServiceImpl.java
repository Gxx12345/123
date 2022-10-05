package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.IService;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品业务层
 *
 * @author yjiiie6
 * @since 2022/9/26 17:45
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {

    @Autowired
    private IDishFlavorService iDishFlavorService;


    /**
     * 新增菜品
     *
     * @param dishDtoParam
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDtoParam) {
        // 1.保存菜品信息
        Dish dish = new Dish();

        // dishDto -> dish
        // 第一个参数是数据源 ， 第二个参数是要赋值的类
        BeanUtils.copyProperties(dishDtoParam,dish);
        save(dish);
        // 保存之后的数据ID
        Long dishId = dish.getId();
        // 2.菜品口味信息
        // 需要给口味信息中的dishId赋值
        // 前端口味信息不是必须要传 ， 所以要校验是否添加了口味信息
        if (CollectionUtils.isNotEmpty(dishDtoParam.getFlavors())) {
            for (DishFlavor flavor : dishDtoParam.getFlavors()) {
                // 给子表中的主表ID赋值
                // 菜品ID
                flavor.setDishId(dishId);
            }
        }

        iDishFlavorService.saveBatch(dishDtoParam.getFlavors());


    }


    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 1 查询菜品
        Dish dish = getById(id);
        if (dish == null) {
            throw new CustomException("传入的参数有误");
        }
        // 2 构建dishDto,赋值
        DishDto dishDto = new DishDto();
        // 第一个参数,是有值的
        // 第二个参数,是要赋值给其的目标 target
        BeanUtils.copyProperties(dish,dishDto);


        // 3 根据这个菜品查询其口味
        // 需要口味service
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 根据菜品ID(dishId)查询这个菜品的口味
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        // 口味信息
        List<DishFlavor> dishFlavorList = iDishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;

    }

    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param dishDtoParam
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDtoParam) {
        // 1.先更新Dish表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDtoParam,dish);
        updateById(dish);

        // 2.删除原来菜品的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 根据菜品id删除其口味信息
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        iDishFlavorService.remove(queryWrapper);

        // 3.把dishId重新赋值给flavors口味
        if (CollectionUtils.isNotEmpty(dishDtoParam.getFlavors())) {
            for (DishFlavor flavor : dishDtoParam.getFlavors()) {
                // 重新给口味信息赋值
                flavor.setDishId(dish.getId());
            }
        }
        // 4.添加新的口味
        iDishFlavorService.saveBatch(dishDtoParam.getFlavors());
    }
}
