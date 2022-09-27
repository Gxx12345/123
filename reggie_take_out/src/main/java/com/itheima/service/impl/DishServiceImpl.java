package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.dto.DishDto;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.mapper.DishMapper;
import com.itheima.service.IDishFlavorService;
import com.itheima.service.IDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜品的业务层
 *
 * @author L
 * @since 2022/9/26 17:57
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {

    @Autowired
    private IDishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //1.保存菜品信息
        Dish dish = new Dish();
        //dishDto -> dish
        //第一个参数是数据源
        //第二个参数是要赋值的类
        //source -> target
        BeanUtils.copyProperties(dishDto,dish);
        this.save(dish);
        //这个id就是保存之后的数据ID
        Long dishId = dish.getId();
        //2.菜品口味信息
        // 需要给口味信息中的dishId赋值
        //我们前端这个口味信息不是必须要传
        //校验前端是否添加了口味信息
        if (CollectionUtils.isNotEmpty(dishDto.getFlavors())) {
            for (DishFlavor flavor : dishDto.getFlavors()) {
                //给字表中的主表ID赋值
                //菜品ID
                flavor.setDishId(dishId);
            }
        }
        this.dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 1.查询菜品
        Dish dish = this.getById(id);
        if (dish == null) {
            throw new CustomException("传入的参数有误");
        }
        //2.构建dishDto，赋值
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //3.根据这个菜品查询其口味
        //需要口味service
        LambdaQueryWrapper<DishFlavor> dishFlavorWrapper = new LambdaQueryWrapper<>();
        //根据菜品ID(dishId)查询这个菜品的口味
        dishFlavorWrapper.eq(DishFlavor::getDishId,dish.getId());
        //口味信息
        List<DishFlavor> dishFlavorsList = this.dishFlavorService.list(dishFlavorWrapper);
        dishDto.setFlavors(dishFlavorsList);
        return dishDto;
    }

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
