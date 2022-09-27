package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.common.CustomException;
import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.entity.DishDto;
import com.alibaba.reggie.entity.DishFlavor;
import com.alibaba.reggie.mapper.DishMapper;
import com.alibaba.reggie.service.IDishFlavorService;
import com.alibaba.reggie.service.IDishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 菜品
 *
 * @author cyberengr
 * @since 2022/9/26 16:42
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    @Autowired
    private IDishFlavorService dishFlavorService;

    /**
     * 新增菜品信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        save(dishDto);
        Long dtoId = dishDto.getId();
        if (dishDto.getFlavors().size() > 0) {
            dishDto.getFlavors().stream().forEach(item ->
                    item.setDishId(dtoId));
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 回显菜品信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = getById(id);
        if (dish == null) {
            throw new CustomException("输入的参数错误");
        }
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 修改菜品信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto,dish);
        updateById(dish);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        dishFlavorService.remove(queryWrapper);
        dishDto.getFlavors().stream().forEach(item->item.setDishId(dish.getId()));
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 删除菜品信息
     * @param ids
     */
    @Transactional
    @Override
    public void deleteByIds(Long[] ids) {
        try {
            this.removeByIds(Arrays.asList(ids));
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DishFlavor::getDishId,ids);
            dishFlavorService.remove(queryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(GlobalConstant.FAILED);
        }
    }
}
