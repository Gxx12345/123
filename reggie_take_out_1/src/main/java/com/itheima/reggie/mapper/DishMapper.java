package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品
 *
 * @author t3rik
 * @since 2022/9/26 16:39
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
