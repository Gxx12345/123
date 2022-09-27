package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

/**
 * 菜品业务层
 *
 * @author yjiiie6
 * @since 2022/9/26 17:44
 */
public interface IDishService extends IService<Dish>{
    /**
     * 新增菜品
     *
     * @param dishDtoParam
     */
    void saveWithFlavor(DishDto dishDtoParam);

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     */
    DishDto getByIdWithFlavor(Long id);


    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param dishDtoParam
     */
    void updateWithFlavor(DishDto dishDtoParam);
}
