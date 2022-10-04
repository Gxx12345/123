package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.BaseContext;
import com.alibaba.reggie.common.CustomException;
import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.entity.ShoppingCart;
import com.alibaba.reggie.service.IShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 *
 * @author cyberengr
 * @since 2022/9/30 10:15
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCartParam
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCartParam) {
        // 1）查询当前用户的购物车中是否有此菜品或套餐的数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getSetThreadLocalCurrentId());
        // 2）判断当前购物车是否存在此菜品或者套餐.
        if (shoppingCartParam.getDishId() != null) {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCartParam.getDishId());
        } else if (shoppingCartParam.getSetmealId() != null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCartParam.getSetmealId());
        } else {
            throw new CustomException("传入的参数不正确");
        }
        ShoppingCart shoppingCart = this.shoppingCartService.getOne(queryWrapper);
        // 3）如果存在,就在原来的数量基础上加一
        if (shoppingCart != null) {
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            // 3.1）更新数据
            this.shoppingCartService.updateById(shoppingCart);
        } else {
            // 4）如果不存在,则添加到购物车,默认数量就是一
            shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(shoppingCartParam, shoppingCart);
            shoppingCart.setNumber(1);
            // 4.1）在threadlocal中取到当前用户的id,赋值给要添加的购物车数据.及当前时间。
            shoppingCart.setUserId(BaseContext.getSetThreadLocalCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            // 4.2） 新增数据
            this.shoppingCartService.save(shoppingCart);
        }
        return Result.success(shoppingCart);
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getSetThreadLocalCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return Result.success(list);
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> clean() {
        //SQL:delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getSetThreadLocalCurrentId());

        shoppingCartService.remove(queryWrapper);
        return Result.success("清空购物车成功");
    }

    /**
     * 删除购物
     *
     * @param shoppingCartParam
     * @return
     */
    @PostMapping("/sub")
    public Result<ShoppingCart> update(@RequestBody ShoppingCart shoppingCartParam) {
        //查询当前用户的购物车中是否有此菜品或套餐的数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getSetThreadLocalCurrentId());
        // 判断当前购物车是否存在此菜品或者套餐.
        if (shoppingCartParam.getDishId() != null) {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCartParam.getDishId());
        } else if (shoppingCartParam.getSetmealId() != null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCartParam.getSetmealId());
        } else {
            throw new CustomException("传入的参数不正确");
        }
        ShoppingCart shoppingCart = this.shoppingCartService.getOne(queryWrapper);
        //如果存在,就在原来的数量基础上减一
        if (shoppingCart.getNumber() > 0) {
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            // 更新数据
            this.shoppingCartService.updateById(shoppingCart);
            if (shoppingCart.getNumber() == 0) {
                this.shoppingCartService.remove(queryWrapper);
            }
            return Result.success(shoppingCart);
        }
        return Result.error(GlobalConstant.FAILED);
    }

}
