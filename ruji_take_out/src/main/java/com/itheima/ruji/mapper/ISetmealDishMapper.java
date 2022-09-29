package com.itheima.ruji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.ruji.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品套餐关系持久层
 *
 * @autho Gzz
 * @since 2022/9/29 11:08
 */
@Mapper
public interface ISetmealDishMapper extends BaseMapper<SetmealDish> {
}
