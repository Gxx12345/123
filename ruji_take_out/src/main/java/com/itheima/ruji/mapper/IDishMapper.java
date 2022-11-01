package com.itheima.ruji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.ruji.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品持久层
 *
 * @author Gzz
 * @since 2022/9/26 16:51
 */

@Mapper
public interface IDishMapper extends BaseMapper<Dish> {
}
