package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.ShoppingCart;

/**
 * 购物车
 *
 * @author Gmy
 * @since 2022/9/30 12:21
 */
public interface ShoppingCartService extends IService<ShoppingCart> {
    /**
     * 添加购物车
     */
    ShoppingCart add(ShoppingCart shoppingCartParam);

    /**
     * 购物车减号
     * @param shoppingCartParam
     * @return
     */
    ShoppingCart  subShoppingCart(ShoppingCart shoppingCartParam);
}