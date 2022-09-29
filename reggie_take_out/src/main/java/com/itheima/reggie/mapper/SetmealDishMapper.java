package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐和菜品关系
 * @author Gmy
 * @since 2022/9/29 11:09
 */
@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
