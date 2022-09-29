package com.itheima.ruji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.ruji.dto.SetmealDto;
import com.itheima.ruji.entity.Setmeal;

import java.util.List;

/**
 * 套餐业务层
 *
 * @autho Gzz
 * @since 2022/9/26 17:46
 */
public interface ISetmealService extends IService<Setmeal> {
    void saveSetmeal(SetmealDto dto);

    void deleteSetmeals(List<Long> ids);
}
