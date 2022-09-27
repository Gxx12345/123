package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.entity.DishDto;
import com.alibaba.reggie.mapper.DishMapper;
import com.alibaba.reggie.service.IDishFlavorService;
import com.alibaba.reggie.service.IDishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        save(dishDto);
        Long dtoId = dishDto.getId();
        if (dishDto.getFlavors().size() > 0) {
            dishDto.getFlavors().stream().forEach(item->
                    item.setDishId(dtoId));
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }
}
