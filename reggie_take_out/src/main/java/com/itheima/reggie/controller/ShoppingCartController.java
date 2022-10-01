package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.IShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 *
 * @author my
 * @since 2022/9/30 12:11
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * 新增购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCartParam){
        //查询当前用户的购物车中是否含有套餐和菜品信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //查询当前登录用户
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        //判断当前购物车是否存在此菜品或者套餐.
        if(shoppingCartParam.getDishId() != null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCartParam.getDishId());
        } else if(shoppingCartParam.getSetmealId() != null){
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCartParam.getSetmealId());
        } else {
            throw new CustomException("传入的参数不正确");
        }
        ShoppingCart shoppingCart = this.shoppingCartService.getOne(queryWrapper);
        //如果存在,就在原来的数量基础上加一
        if(shoppingCart!=null){
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            this.shoppingCartService.updateById(shoppingCart);
        } else{
            //如果不存在,则添加到购物车,默认数量就是一
            shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(shoppingCartParam,shoppingCart);
            shoppingCart.setNumber(1);
            //在threadlocal中取到当前用户的id,赋值给要添加的购物车数据.及当前时间。
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.shoppingCartService.save(shoppingCart);
        }
        return R.success(shoppingCart);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = this.shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //SQL:delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        this.shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

    /**
     * 取消菜品或套餐
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCartParam){
        if(shoppingCartParam.getNumber()==null){
            throw new CustomException("参数有误");
        }
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        if (shoppingCartParam.getDishId()!=null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCartParam.getDishId());
        } else if(shoppingCartParam.getSetmealId()!=null){
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCartParam.getSetmealId());
        } else {
            throw new CustomException("传入参数有误");
        }
        ShoppingCart shoppingCart = this.shoppingCartService.getOne(queryWrapper);
        if(shoppingCart.getNumber() > 1){
            shoppingCart.setNumber(shoppingCart.getNumber()-1);
            this.shoppingCartService.updateById(shoppingCart);
            return R.success(GlobalConstant.FINISH);
        } else {
            this.shoppingCartService.remove(queryWrapper);
        }
        return R.success(GlobalConstant.FINISH);
    }
}
