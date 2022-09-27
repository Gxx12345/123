package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * 菜品
 *
 * @author my
 * @since 2022/9/26 17:39
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    @Autowired
    private IDishFlavorService dishFlavorService;
    /**
     * 保存菜品信息
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品信息
        Dish dish = new Dish();
        //dishDto -> dish
        BeanUtils.copyProperties(dishDto,dish);
        this.save(dish);
        //这个ID就是保存之后的数据ID
        Long dishId = dish.getId();
        //菜品口味信息
        //校验口味信息
        if(CollectionUtils.isNotEmpty(dishDto.getFlavors())){
            for (DishFlavor flavor : dishDto.getFlavors()) {
                flavor.setDishId(dishId);
            }
        }
        this.dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    //根据ID查询菜品信息
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavors = this.dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 修改菜品信息
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        Dish dish = new Dish();
        // 在dto中取到我们更新的值
        BeanUtils.copyProperties(dishDto, dish);
        // 更新dish表
        this.updateById(dish);
        // 2.删除菜品口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 根据菜品id删除掉,所有与他有关的菜品口味
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        this.dishFlavorService.remove(queryWrapper);
        // 添加新的口味
        // 批量新增新的口味
        // 3.要给flavors 口味表中的dishId赋值
        dishDto.getFlavors().forEach(item -> item.setDishId(dish.getId()));
        // 4.新增口味
        this.dishFlavorService.saveBatch(dishDto.getFlavors());
    }
}
