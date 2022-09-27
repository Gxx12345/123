package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

/**
 * 菜品
 *
 * @author my
 * @since 2022/9/26 17:38
 */
public interface IDishService extends IService<Dish> {
    /**
     * 保存菜品信息
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据ID查询菜品信息
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     *修改菜品信息
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);
}
