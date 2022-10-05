package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.IDishService;
import com.itheima.reggie.service.IShoppingCartService;
import jdk.nashorn.internal.runtime.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车控制层
 *
 * @author yjiiie6
 * @since 2022/9/30 12:16
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService iShoppingCartService;



    /**
     * 添加购物车
     *
     * @param shoppingCartParam
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCartParam) {

        // 1）查询当前用户的购物车中是否有此菜品或套餐的数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());

        // 2）判断当前购物车是否存在此菜品或者套餐.
        if (shoppingCartParam.getDishId() != null) {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCartParam.getDishId());
        } else if (shoppingCartParam.getSetmealId() != null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCartParam.getSetmealId());
        } else {
            throw new CustomException("传入的参数有误");
        }

        ShoppingCart shoppingCart = iShoppingCartService.getOne(queryWrapper);
        // 3）如果存在,就在原来的数量基础上加一
        if (shoppingCart != null) {
            // 3.1）更新数据
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            iShoppingCartService.updateById(shoppingCart);
        } else {
            // 4）如果不存在,则添加到购物车,默认数量就是一
            shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(shoppingCartParam, shoppingCart);
            shoppingCart.setNumber(1);
            // 4.1）在threadlocal中取到当前用户的id,赋值给要添加的购物车数据.及当前时间。
            shoppingCart.setUserId(BaseContext.getCurrentUserId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            // 4.2） 新增数据
            iShoppingCartService.save(shoppingCart);
        }

        return R.success(shoppingCart);
    }


    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> shoppingCartList = iShoppingCartService.list(queryWrapper);
        return R.success(shoppingCartList);
    }


    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());

        iShoppingCartService.remove(queryWrapper);
        return R.success(GlobalConstant.FINISH);
    }


    /**
     * 购物车 ‘减号按钮’  ---  未实现
     * @param shoppingCartParam
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> delete(@RequestBody ShoppingCart shoppingCartParam) {
        log.info("前后端联通");
        if (shoppingCartParam.getSetmealId() == null && shoppingCartParam.getDishId() == null) {
            throw new CustomException("传入的参数有误");
        }
        return R.success(iShoppingCartService.subShoppingCart(shoppingCartParam));
    }
}
