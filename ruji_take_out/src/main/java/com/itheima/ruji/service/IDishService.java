package com.itheima.ruji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.ruji.dto.DishDto;
import com.itheima.ruji.entity.Dish;

/**
 * 菜品业务层
 *
 * @autho Gzz
 * @since 2022/9/26 16:52
 */
public interface IDishService extends IService<Dish> {
    /**
     * 保存菜品及菜品口味信息
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);
    /**
     * 根据id查询菜品及口味信息
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);
    /**
     * 更新菜品信息及其口味信息
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);
}
