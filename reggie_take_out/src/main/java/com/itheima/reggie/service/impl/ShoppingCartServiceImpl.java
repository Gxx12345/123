package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 购物车
 *
 * @author Gmy
 * @since 2022/9/30 14:23
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    /**
     * 添加购物车
     *
     * @param shoppingCartParam
     */
    @Override
    public ShoppingCart add(ShoppingCart shoppingCartParam) {
        //  添加购物车有两种情况
        //  A 购物车中无任何菜品，可以直接添加
        //  B 购物车中已经有了相同的菜品，要在这个相同的菜品上+1
        //  1.查询当前的购物车中是否有此菜品或套餐的数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //  查询当前用户的购物车数据
        Long currentUserId = BaseContext.getCurrentUserId();
        //  当前用户id
        queryWrapper.eq(ShoppingCart::getUserId, currentUserId);
        //  还需要判断，当前传入的数据是菜品还是套餐
        //  菜品和套餐是互斥的
        //  有菜品就没有套餐，有套餐就没有菜品
        if (shoppingCartParam.getDishId() != null) {
            //  菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCartParam.getDishId());
        } else if (shoppingCartParam.getSetmealId() != null) {
            //  套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCartParam.getSetmealId());
        } else {
            throw new CustomException("传入的参数不正确");
        }
        //  当前购物车中是否有相同的菜品
        ShoppingCart shoppingCart = this.getOne(queryWrapper);
        //  2.判断当前购物车是否存在此菜品或者套餐
        if (shoppingCart != null) {
            //  3.如果存在，就在原来的数量基础上+1
            shoppingCart.setNumber(shoppingCartParam.getNumber() != null ?
                    shoppingCart.getNumber() + shoppingCartParam.getNumber()
                    : shoppingCart.getNumber() +1);
            //  3.1更新数据
            this.updateById(shoppingCart);
        } else {
            //  4.如果不存在，则添加到购物车，默认数量就是1
            shoppingCartParam.setNumber(shoppingCartParam.getNumber() != null ? shoppingCartParam.getNumber() : 1);
            //  4.1在threadlocal中取到当前用户的id，赋值给要添加的购物车数据
            shoppingCartParam.setSetmealId(BaseContext.getCurrentUserId());
            shoppingCartParam.setCreateTime(LocalDateTime.now());
            //  4.2新增数据
            this.save(shoppingCartParam);
            shoppingCart = shoppingCartParam;
        }
        return shoppingCart;
    }

    /**
     * 购物车减号
     *
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
            throw new CustomException(GlobalConstant.FAILED);
        }
        ShoppingCart shoppingCart = this.getOne(queryWrapper);
        Optional.ofNullable(shoppingCart).orElseThrow(() -> new CustomException(GlobalConstant.ERROR_PARAM));
        if (shoppingCart.getNumber() == 1) {
            this.removeById(shoppingCart);
            shoppingCart.setNumber(0);
        } else {
            shoppingCart.setNumber(shoppingCart.getNumber() -1);
            this.updateById(shoppingCart);
        }
        return shoppingCart;
    }
}
