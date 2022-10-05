package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * 套餐
 *
 * @author my
 * @since 2022/9/26 17:41
 */
public interface ISetmealService extends IService<Setmeal> {
    /**
     * 添加套餐
     * @param dto
     */
    void saveWithDish(SetmealDto dto);

    /**
     * 删除套餐
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    /**
     * 根据ID查询套餐及其菜品
     * @param id
     * @return
     */
    SetmealDto getByIdWithDish(Long id);

    /**
     * 修改
     */
    void updateWithDish(SetmealDto setmealDto);
}
