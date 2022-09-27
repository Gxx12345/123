package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品
 *
 * @author my
 * @since 2022/9/26 17:36
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}