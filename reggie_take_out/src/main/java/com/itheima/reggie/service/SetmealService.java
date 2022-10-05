package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * 套餐业务层
 *
 * @author Gmy
 * @since 2022/9/26 17:59
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐
     * @param dto
     */
    void saveWithDish(SetmealDto dto);

    /**
     * 删除套餐
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id查询套餐
     * @param id
     */
    SetmealDto getSetmealById(Long id);

    /**
     * 更新套餐
     * @param setmealDto
     * @return
     */
    Boolean updateWithDish(SetmealDto setmealDto);
}
