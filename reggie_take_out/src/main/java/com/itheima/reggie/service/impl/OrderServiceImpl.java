package com.itheima.reggie.service.impl;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单业务层实现类
 *
 * @author Gmy
 * @since 2022/9/30 16:58
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    /**
     *地址薄
     */
    @Autowired
    private IAddressBookService addressBookService;
    /**
     * 购物车
     */
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 用户下单
     *
     * @param orders
     */
    @Override
    public void submit(Orders orders) {
        // 1.查询地址数据
        Long addressBookId = orders.getAddressBookId();
        // 查询地址
        AddressBook addressBook = this.addressBookService.getById(addressBookId);
        // 地址校验
        if (addressBook == null) {
            throw new CustomException("地址信息为空,无法下单");
        }
        // 2.查询购物车数据
        // user_id
        LambdaQueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new LambdaQueryWrapper<>();
        // 当前登录用户id查询当前登录用的购物车
        shoppingCartQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());
        // 查询购物车
        List<ShoppingCart> shoppingCartList = this.shoppingCartService.list(shoppingCartQueryWrapper);
        // 如果购物车中没有查到任何内容,不允许下单
        if (CollectionUtils.isEmpty(shoppingCartList)) {
            throw new CustomException("购物车中无任何菜品,不允许下单");
        }
        //  主表id
        long orderId = IdWorker.getId();
        Integer totalPrice = 0;
        // 3.组装订单明细,并给订单明细表中的主表id赋值，使其与主表产生关系
        // 遍历购物车集合
        // 订单明细集合
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            // shoppingCart -> orderDetail
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            // 主表id
            orderDetail.setOrderId(orderId);
            // 4.计算订单总价值
            // 单价
            // 总价 = 单价*数量
            int price = shoppingCart.getAmount()
                    .multiply(new BigDecimal(shoppingCart.getNumber())).intValue();
            // 订单的总价
            totalPrice += price;
            // 把订单加到订单明细集合
            orderDetailList.add(orderDetail);
        }
        log.info("订单明细组装完成");
        // 5.组装订单
        Orders ordersMain = new Orders();
        // 主键id
        ordersMain.setId(orderId);
        // 订单号 往往是有其意义的
        // CZ + 202209301616 + 002
        ordersMain.setNumber(orderId + "");
        // 订单状态 // 状态机模式
        ordersMain.setStatus(2);
        // 登录用户id
        ordersMain.setUserId(BaseContext.getCurrentUserId());
        // 地址簿id
        ordersMain.setAddressBookId(orders.getAddressBookId());
        // 下单时间
        ordersMain.setOrderTime(LocalDateTime.now());
        // 结账时间
        ordersMain.setCheckoutTime(LocalDateTime.now());
        // 支付方式
        ordersMain.setPayMethod(orders.getPayMethod());
        // 订单总价值
        ordersMain.setAmount(new BigDecimal(totalPrice));
        // 订单备注
        ordersMain.setRemark(orders.getRemark());
        // 当前登录人的名称
        User user = this.userService.getById(BaseContext.getCurrentUserId());
        ordersMain.setUserName(user.getName());
        // 收货人手机号
        ordersMain.setPhone(addressBook.getPhone());
        // 收货人地址
        ordersMain.setAddress(addressBook.getDetail());
        // 收货人名称
        ordersMain.setConsignee(addressBook.getConsignee());
        // 6.保存订单
        this.save(ordersMain);
        // 7.保存订单明细
        this.orderDetailService.saveBatch(orderDetailList);
        // 8.删除购物车数据
        LambdaQueryWrapper<ShoppingCart> removeQueryWrapper = new LambdaQueryWrapper<>();
        // 当前登录用户删除当前登录用户的购物车
        shoppingCartQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());
        this.shoppingCartService.remove(removeQueryWrapper);
    }
}
