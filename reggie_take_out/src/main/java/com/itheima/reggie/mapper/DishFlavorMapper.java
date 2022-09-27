package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品口味持久层
 *
 * @author yjiiie6
 * @since 2022/9/27 12:04
 */
@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
}
