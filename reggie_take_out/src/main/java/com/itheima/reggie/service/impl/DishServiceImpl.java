package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.datatransfer.FlavorEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜品的业务层实现类
 *
 * @author Gmy
 * @since 2022/9/26 18:00
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
   private DishFlavorService dishFlavorService;
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //  1.保存菜品信息
        Dish dish = new Dish();
        //  dishDto -> dish
        //  第一个参数是数据源
        //  第二个参数就是要赋值的类
        //  source -> target
        BeanUtils.copyProperties(dishDto,dish);
        this.save(dish);
        //  这个id就是保存之后的数据ID
        Long dishId = dish.getId();
        //  2. 菜品口味信息
        //  需要给口味信息中的dishID赋值
        //  我们前端这个口味信息不是必须要传的
        //  校验前端是否添加了口味信息
        if (CollectionUtils.isNotEmpty(dishDto.getFlavors())) {
            for (DishFlavor flavor : dishDto.getFlavors()) {
                //  给子表中的主表ID赋值
                //  菜品ID
                flavor.setDishId(dishId);
            }
            this.dishFlavorService.saveBatch(dishDto.getFlavors());
        }
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //1.查询菜品基本信息
        Dish dish = this.getById(id);
        //2.构建DishDto，把Dish对象复制到DishDto对象
        DishDto dishDto = new DishDto();
        // 第一个参数,是有值的
        // 第二个参数,是要赋值给其的目标 target
        BeanUtils.copyProperties(dish,dishDto);
        //3.查询当前菜品对应的口味信息，赋值给DishDto对象
        // 需要口味service
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 根据菜品ID(dishId)查询这个菜品的口味
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        // 口味信息
        List<DishFlavor> dishFlavors = this.dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavors);
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
        //1.先更新Dish表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto,dish);
        updateById(dish);
        //2.删除原来菜品的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 根据菜品id删除其口味信息
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        this.dishFlavorService.remove(queryWrapper);
        //3.添加新的口味
        if (CollectionUtils.isNotEmpty(dishDto.getFlavors())) {
            for (DishFlavor item : dishDto.getFlavors()) {
                // 重新给口味信息赋值
                item.setDishId(dish.getId());
            }
        }
        //  4. 添加新的口味
        this.dishFlavorService.saveBatch(dishDto.getFlavors());
    }
}
