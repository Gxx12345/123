package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.ShoppingCart;

/**
 * 购物车业务层
 *
 * @author yjiiie6
 * @since 2022/9/30 12:14
 */
public interface IShoppingCartService extends IService<ShoppingCart> {
    /**
     * 购物车减号
     * @param shoppingCartParam
     * @return
     */
    ShoppingCart subShoppingCart(ShoppingCart shoppingCartParam);



}
