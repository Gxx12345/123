package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车
 *
 * @author my
 * @since 2022/9/30 12:12
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
