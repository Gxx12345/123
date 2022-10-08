package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * 套餐业务层
 *
 * @author t3rik
 * @since 2022/9/26 16:42
 */
public interface ISetmealService extends IService<Setmeal> {
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
}
