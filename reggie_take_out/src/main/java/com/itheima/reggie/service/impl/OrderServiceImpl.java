package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.IAddressBookService;
import com.itheima.reggie.service.IOrderService;
import com.itheima.reggie.service.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单
 *
 * @author my
 * @since 2022/9/30 16:58
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements IOrderService {
    @Autowired
    private IAddressBookService addressBookService;

    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * 用户下单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //查询地址数据
        AddressBook addressBook = this.addressBookService.getById(orders.getAddressBookId());
        if(addressBook == null){
            throw new CustomException("传入的参数有误");
        }
        //查询购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();
        shoppingCartWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = this.shoppingCartService.list(shoppingCartWrapper);
        if(CollectionUtils.isEmpty(shoppingCartList)){
            throw new CustomException("购物车为空,不能下单");
        }

    }
}
