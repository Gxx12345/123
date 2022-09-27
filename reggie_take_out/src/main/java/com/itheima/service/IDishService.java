package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.DishDto;
import com.itheima.entity.Dish;

/**
 * 菜品
 *
 * @author L
 * @since 2022/9/26 17:56
 */
public interface IDishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
