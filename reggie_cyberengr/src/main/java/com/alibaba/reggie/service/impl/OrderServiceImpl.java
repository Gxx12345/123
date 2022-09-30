package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.common.BaseContext;
import com.alibaba.reggie.common.CustomException;
import com.alibaba.reggie.entity.*;
import com.alibaba.reggie.mapper.OrderMapper;
import com.alibaba.reggie.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderServiceImpl
 *
 * @author cyberengr
 * @since 2022/9/30 10:21
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements IOrderService {
    @Autowired
    private IAddressBookService addressBookService;
    @Autowired
    private IShoppingCartService shoppingCartService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IOrderDetailService orderDetailService;

    /**
     * 下单
     *
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        // 1.查询地址数据
        AddressBook addressBook = this.addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("传入的参数不正确");
        }
        // 2.查询购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();
        shoppingCartWrapper.eq(ShoppingCart::getUserId, BaseContext.getSetThreadLocalCurrentId());
        List<ShoppingCart> shoppingCartList = this.shoppingCartService.list(shoppingCartWrapper);
        if (CollectionUtils.isEmpty(shoppingCartList)) {
            throw new CustomException("购物车为空,不能下单");
        }
        // 3.组装订单明细,并给订单明细表中的主表id赋值，使其与主表产生关系
        Long orderId = IdWorker.getId();
        Integer totalAmount = 0;
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orderId);
            // 4.计算订单总价值
            // 单个商品总价 = 单价 * 数量
            BigDecimal price = shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber()));
            totalAmount += price.intValue();
            orderDetailList.add(orderDetail);
        }
        // 5.组装订单
        // 主表
        Orders ordersMain = new Orders();
        // 主表ID
        ordersMain.setId(orderId);
        // 订单号
        ordersMain.setNumber(orderId + "");
        // 订单状态
        ordersMain.setStatus(2);
        // 当前用户ID
        ordersMain.setUserId(BaseContext.getSetThreadLocalCurrentId());
        // 当前订单派送地址
        ordersMain.setAddressBookId(orders.getAddressBookId());
        // 订单时间
        ordersMain.setOrderTime(LocalDateTime.now());
        // 结账时间
        ordersMain.setCheckoutTime(LocalDateTime.now());
        // 支付方式
        ordersMain.setPayMethod(orders.getPayMethod());
        // 订单的总价值
        ordersMain.setAmount(new BigDecimal(totalAmount));
        // 备注
        ordersMain.setRemark(orders.getRemark());
        // 用户名
        User user = userService.getById(BaseContext.getSetThreadLocalCurrentId());
        ordersMain.setUserName(user.getName());
        // 收货人的手机号
        ordersMain.setPhone(addressBook.getPhone());
        ordersMain.setAddress(addressBook.getDetail());
        ordersMain.setConsignee(addressBook.getConsignee());
        // 6.保存订单
        this.save(ordersMain);
        // 7.保存订单明细
        this.orderDetailService.saveBatch(orderDetailList);
        // 8.删除购物车数据
        this.shoppingCartService.remove(shoppingCartWrapper);
    }
}
