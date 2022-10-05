package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.controller.ShoppingCartController;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单业务层
 *
 * @author yjiiie6
 * @since 2022/10/1 10:35
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    @Autowired
    private IAddressBookService iAddressBookService;
    @Autowired
    private IShoppingCartService iShoppingCartService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderDetailService iOrderDetailService;
    @Autowired
    private ShoppingCartController shoppingCartController;


    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        // 1.查询地址数据
        AddressBook addressBook = iAddressBookService.getById(orders.getAddressBookId());
        // 地址校验
        if (addressBook == null) {
            throw new CustomException("地址信息为空，不允许下单");
        }
        // 2.查询购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());
        List<ShoppingCart> shoppingCartList = iShoppingCartService.list(queryWrapper);
        // 如果购物车中没有查到任何数据，则不允许下单
        if (CollectionUtils.isEmpty(shoppingCartList)) {
            throw new CustomException("购物车中无任何菜品，不允许下单");
        }
        // 3.组装订单明细 , 并给订单明细表中的主表id赋值，使其与主表产生关系
        // 主表id
        long orderId = IdWorker.getId();
        Integer totalAmount = 0;
        // 订单明细集合
        List<OrderDetail> orderDetailList = new ArrayList<>();
        // 遍历购物车集合
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            // 主表id
            orderDetail.setOrderId(orderId);
            // 4.计算订单总价值
            BigDecimal price = shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber()));
            totalAmount += price.intValue();
            // 把订单加到订单明细集合
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
        ordersMain.setUserId(BaseContext.getCurrentUserId());
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
        User user = iUserService.getById(BaseContext.getCurrentUserId());
        ordersMain.setUserName(user.getName());
        // 收货人的手机号
        ordersMain.setPhone(addressBook.getPhone());
        // 收货人的地址
        ordersMain.setAddress(addressBook.getDetail());
        // 收货人
        ordersMain.setConsignee(addressBook.getConsignee());
        // 6.保存订单
        save(ordersMain);
        // 7.保存订单明细
        iOrderDetailService.saveBatch(orderDetailList);
        // 8.删除购物车数据
        LambdaQueryWrapper<ShoppingCart> removeQueryWrapper = new LambdaQueryWrapper<>();
        // 当前登录用户id查询当前登录用户的购物车
        removeQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentUserId());
        iShoppingCartService.remove(removeQueryWrapper);
    }


    /**
     * 订单明细分页查询
     *
     * @param page      当前页
     * @param pageSize  当前页记录数
     * @param number    订单号
     * @param beginTime 下单查询起始时间
     * @param endTime   下单查询最后时间
     * @return
     */
    @Override
    public Page<Orders> getPage(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        Page<Orders> queryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(number), Orders::getNumber, number);
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.between(Orders::getOrderTime, beginTime, endTime);
        }
        queryWrapper.orderByDesc(Orders::getOrderTime);

        return page(queryPage, queryWrapper);
    }


    /**
     * 再来一单
     *
     * @param orders
     * @return
     */
    @Override
    public boolean againOrder(Orders orders) {
        Optional.ofNullable(orders.getId())
                .orElseThrow(() -> new CustomException("传入的参数有误"));
        Orders oldOrder = getById(orders.getId());
        Optional.ofNullable(oldOrder)
                .orElseThrow(() -> new CustomException("传入的参数有误"));

        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orders.getId());
        List<OrderDetail> orderDetailList = iOrderDetailService.list(queryWrapper);
        if (CollectionUtils.isEmpty(orderDetailList)) {
            throw new CustomException("传入的参数有误,请重新选择商品后下单");
        }
        orderDetailList.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id");
            shoppingCart.setUserId(BaseContext.getCurrentUserId());
            shoppingCartController.add(shoppingCart);
        });
        return Boolean.TRUE;
    }


    /**
     * 个人中心 历史订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrdersDto> getUserPage(Integer page, Integer pageSize) {
        Page<Orders> queryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentUserId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        Page<Orders> ordersPage = page(queryPage, queryWrapper);
        Page<OrdersDto> result = new Page<>(page, pageSize);
        // 没有查询到记录的话,直接返回
        if (CollectionUtils.isEmpty(ordersPage.getRecords())) {
            return result;
        }
        BeanUtils.copyProperties(ordersPage, result, "records");
        List<OrdersDto> orderDtoList = ordersPage.getRecords().stream().map(item -> {
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(item, dto);
            // 查询子订单信息
            LambdaQueryWrapper<OrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
            orderDetailWrapper.eq(OrderDetail::getOrderId, item.getId());
            // 子订单数据
            List<OrderDetail> orderDetails = iOrderDetailService.list(orderDetailWrapper);
            dto.setOrderDetails(orderDetails);
            return dto;
        }).collect(Collectors.toList());
        result.setRecords(orderDtoList);
        return result;
    }
}
