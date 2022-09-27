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
    /**
     * 新增菜品信息
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 回显菜品信息
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品信息
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);
}
