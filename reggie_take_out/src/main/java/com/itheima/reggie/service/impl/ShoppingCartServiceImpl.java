package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.IShoppingCartService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 购物车业务层
 *
 * @author yjiiie6
 * @since 2022/9/30 12:15
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {

    /**
     * 购物车减号
     * @param shoppingCartParam
     * @return
     */
    @Override
    public ShoppingCart subShoppingCart(ShoppingCart shoppingCartParam) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());
        if (shoppingCartParam.getDishId() != null) {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCartParam.getDishId());
        } else if (shoppingCartParam.getSetmealId() != null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCartParam.getSetmealId());
        } else {
            throw new CustomException("传入的参数有误");
        }
        ShoppingCart shoppingCart = getOne(queryWrapper);
        Optional.ofNullable(shoppingCart).orElseThrow(() -> new CustomException("传入的参数有误"));

        if (shoppingCart.getNumber() == 1) {
            removeById(shoppingCart);
            shoppingCart.setNumber(0);
        } else {
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            updateById(shoppingCart);
        }
        return shoppingCart;
    }


}
