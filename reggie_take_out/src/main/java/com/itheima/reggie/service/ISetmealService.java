package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;

import java.util.List;

/**
 * 套餐菜品业务层
 *
 * @author yjiiie6
 * @since 2022/9/26 17:50
 */
public interface ISetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);


    /**
     * 删除套餐
     * @param ids
     */
    void deleteByIds(List<Long> ids);


    /**
     * 根据id查询套餐及对应的菜品
     * @param id
     */
    SetmealDto getByIdWithFlavor(Long id);


    /**
     * 更新套餐
     * @param setmealDto
     */
    void updateWithDish(SetmealDto setmealDto);
}