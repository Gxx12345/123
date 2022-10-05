package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车持久层
 *
 * @author yjiiie6
 * @since 2022/9/30 12:14
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
