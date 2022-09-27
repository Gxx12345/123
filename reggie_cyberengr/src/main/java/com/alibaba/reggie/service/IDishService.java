package com.alibaba.reggie.service;

import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.entity.DishDto;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 菜品
 *
 * @author cyberengr
 * @since 2022/9/26 16:41
 */
public interface IDishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);
}
