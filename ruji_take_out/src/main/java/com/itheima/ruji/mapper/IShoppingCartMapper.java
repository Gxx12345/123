package com.itheima.ruji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.ruji.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车持久层
 *
 * @autho Gzz
 * @since 2022/9/30 12:19
 */
@Mapper
public interface IShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
