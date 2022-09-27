package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品的持久层
 *
 * @author L
 * @since 2022/9/26 17:55
 */
@Mapper
public interface IDishMapper extends BaseMapper<Dish> {
}
