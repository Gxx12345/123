package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 *
 * @author Gmy
 * @since 2022/9/30 14:25
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 新增购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCartParam) {
        log.info("前后端联通");
        //  1.查询当前用户的购物车中是否有此菜品或套餐的数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //  当前登录用户条件
        // BaseContext.getCurrentUserId() = 当前登录用户id
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());
        //  2.判断当前购物车是否存在此菜品或者套餐
        //  菜品id
        if (shoppingCartParam.getDishId() != null) {
            //  加入菜品id条件
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCartParam.getDishId());
        } else if (shoppingCartParam.getSetmealId() != null) {
            //  判断套餐
            //  加入套餐id条件
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCartParam.getSetmealId());
        } else {
            throw new CustomException("传入的参数有误");
        }
        //  查询数据
        ShoppingCart shoppingCart = this.shoppingCartService.getOne(queryWrapper);
        //  3.如果存在，就在原来的数量基础上加以
        if (shoppingCart != null) {
            //  购物车数量 + 1
            //  1 -> 2
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            //  3.1更新数据
            this.shoppingCartService.updateById(shoppingCart);
        } else {
            shoppingCart = new ShoppingCart();
            // shoppingCartParam -> shoppingCart
            BeanUtils.copyProperties(shoppingCartParam,shoppingCart);
            //  4.如果不存在，则添加到购物车，默认数量就是一
            shoppingCart.setNumber(1);
            //  4.1在threadlocal中取到当前用户的id，赋值给要添加的购物车数据，及当前时间
            shoppingCart.setUserId(BaseContext.getCurrentUserId());
            //  当前时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            //  新增数据
            this.shoppingCartService.save(shoppingCart);
        }
        return R.success(shoppingCart);
    }

    /**
     * 查看当前购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getList() {
        log.info("前后端联通");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //  当前登录用户的ID
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        //  当前登录用户的购物车数据
        List<ShoppingCart> list = this.shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 删除购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        log.info("前后端联通");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //  当前登录用户ID
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());
        //  当前登录用户的购物车数据
        this.shoppingCartService.remove(queryWrapper);
        return R.success(GlobalConstant.FINISH);
    }
}
