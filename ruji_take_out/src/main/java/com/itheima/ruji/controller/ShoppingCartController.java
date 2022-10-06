package com.itheima.ruji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.BaseTreadlock;
import com.itheima.ruji.common.CustomException;
import com.itheima.ruji.common.R;
import com.itheima.ruji.entity.ShoppingCart;
import com.itheima.ruji.service.IShoppingCartService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车控制层
 *
 * @author Gzz
 * @since 2022/9/30 12:22
 */

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService iShoppingCartService;

    /**
     * 新增购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart>saveShop(@RequestBody ShoppingCart shoppingCartParam){
        log.info("前后联通:{}",shoppingCartParam.toString());
        // 1）查询当前用户的购物车中是否有此菜品或套餐的数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        // 当前登录用户条件
        // BaseContext.getCurrentUserId() = 当前登录用户id
        wrapper.eq(ShoppingCart::getUserId, BaseTreadlock.getCurrentId());
        // 2）判断当前购物车是否存在此菜品或者套餐.
        // 菜品id
        if (shoppingCartParam.getDishId()!=null){
            // 加入菜品id条件
            wrapper.eq(ShoppingCart::getDishId,shoppingCartParam.getDishId());
        }else if(shoppingCartParam.getSetmealId()!=null){
            // 判断套餐
            // 加入套餐id条件
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCartParam.getSetmealId());
        }else {
            throw new CustomException("输入数据有误");
        }
        // 查询数据
        ShoppingCart one = iShoppingCartService.getOne(wrapper);
        if (one==null){
             one=new ShoppingCart();
            // shoppingCartParam -> shoppingCart
            BeanUtils.copyProperties(shoppingCartParam,one);
            // 4）如果不存在,则添加到购物车,默认数量就是一
            one.setNumber(1);
            // 4.1）在threadlocal中取到当前用户的id,赋值给要添加的购物车数据.及当前时间。
            one.setUserId(BaseTreadlock.getCurrentId());
            // 当前时间
            one.setCreateTime(LocalDateTime.now());
            // 4.2） 新增数据
            iShoppingCartService.save(one);
        }else {
            // 如果存在,就在原来的数量基础上加一
            // 购物车数量 + 1
            // 1 -> 2
            one.setNumber(one.getNumber()+1);
            // 3.1）更新数据
            iShoppingCartService.updateById(one);
        }
        return R.success(one);
    }
    /**
     * 查看当前购物车
     *
     * @return
     */
@GetMapping("/list")
    public R<List<ShoppingCart>>getList(){
    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    // 当前登录用户ID
    wrapper.eq(ShoppingCart::getUserId,BaseTreadlock.getCurrentId());
    wrapper.orderByAsc(ShoppingCart::getCreateTime);
    // 当前登录用户的购物车数据
    List<ShoppingCart> list = iShoppingCartService.list(wrapper);
     return R.success(list);
}
    /**
     * 删除购物车
     *
     * @return
     */
@DeleteMapping("/clean")
public R<String>deleteShop(){
    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    // 当前登录用户ID
    wrapper.eq(ShoppingCart::getUserId,BaseTreadlock.getCurrentId());
    // 当前登录用户的购物车数据
     iShoppingCartService.remove(wrapper);
    return R.success(AntPathmathcherSS.FINISH);
}

    /**
     * 修改购物车
     * @param shoppingCartParam
     * @return
     */
    @PostMapping("/sub")
  public R<ShoppingCart>update(@RequestBody ShoppingCart shoppingCartParam){
    log.info("前后联通:{}",shoppingCartParam.toString());
    // 1）查询当前用户的购物车中是否有此菜品或套餐的数据
    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    // 当前登录用户条件
    // BaseContext.getCurrentUserId() = 当前登录用户id
    wrapper.eq(ShoppingCart::getUserId, BaseTreadlock.getCurrentId());
    if (shoppingCartParam.getDishId()!=null){
        // 加入菜品id条件
        wrapper.eq(ShoppingCart::getDishId,shoppingCartParam.getDishId());
    }else if(shoppingCartParam.getSetmealId()!=null){
        // 判断套餐
        // 加入套餐id条件
        wrapper.eq(ShoppingCart::getSetmealId,shoppingCartParam.getSetmealId());
    }else {
        throw new CustomException("输入数据有误");
    }
    ShoppingCart one = iShoppingCartService.getOne(wrapper);
    if (one.getNumber()==1){
        iShoppingCartService.removeById(one);
        one.setNumber(0);
    }else{
        one.setNumber(one.getNumber()-1);
        iShoppingCartService.updateById(one);
    }
    return R.success(one);
  }
}
