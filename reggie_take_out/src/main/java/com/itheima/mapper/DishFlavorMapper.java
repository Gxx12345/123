package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 口味的持久层
 *
 * @author L
 * @since 2022/9/27 14:57
 */
@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
}
