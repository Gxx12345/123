package com.alibaba.reggie.mapper;

import com.alibaba.reggie.entity.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品
 *
 * @author cyberengr
 * @since 2022/9/26 16:40
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
