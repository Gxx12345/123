package com.alibaba.reggie.service;

import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.dto.SetmealDto;
import com.alibaba.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 套餐
 *
 * @author cyberengr
 * @since 2022/9/26 16:44
 */
public interface ISetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    Page<SetmealDto> getPage(Long page, Long pageSize, String name);

    void deleteByIds(List<Long> ids);
}
