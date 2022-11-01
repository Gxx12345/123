package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.BaseTreadlock;
import com.itheima.ruji.common.CustomException;
import com.itheima.ruji.dto.OrderdsDto;
import com.itheima.ruji.entity.*;
import com.itheima.ruji.mapper.IOrdersMapper;
import com.itheima.ruji.service.*;
import com.sun.prism.impl.BaseContext;
import jdk.nashorn.internal.runtime.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单业务层实体类
 *
 * @author Gzz
 * @since 2022/9/30 17:17
 */

@Service
@Slf4j
public class OrdersImpl extends ServiceImpl<IOrdersMapper, Orders>implements IOrdersService {
  @Autowired
  private IOrderDetailService iOrderDetailService;
  @Autowired
  private IAddressBookService iAddressBookService;
  @Autowired
  private IShoppingCartService iShoppingCartService;
    @Autowired
    private IUserService userService;
    @Override
    @Transactional
    public void submit(Orders ordersParam) {
        // 1.查询地址数据
        Long addressBookId = ordersParam.getAddressBookId();
        // 查询地址
        AddressBook addressBook = iAddressBookService.getById(ordersParam.getAddressBookId());
        // 地址校验
        if (addressBook == null) {
            throw new CustomException("传入的参数不正确");
        }
        // 2.查询购物车数据
        // user_id
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();
        // 当前登录用户id查询当前登录用的购物车
        shoppingCartWrapper.eq(ShoppingCart::getUserId, BaseTreadlock.getCurrentId());
        // 查询购物车
        List<ShoppingCart> list = iShoppingCartService.list(shoppingCartWrapper);
        // 如果购物车中没有查到任何内容,不允许下单
        if (CollectionUtils.isEmpty(list)){
            throw new CustomException("购物车为空,不能下单");
        }
        //  主表id
        long orderId = IdWorker.getId();
        Integer totalPrice = 0;
        // 3.组装订单明细,并给订单明细表中的主表id赋值，使其与主表产生关系
        // 遍历购物车集合
        // 订单明细集合
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : list) {
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
        ordersMain.setUserId(BaseTreadlock.getCurrentId());
        // 地址簿id
        ordersMain.setAddressBookId(ordersParam.getAddressBookId());
        // 下单时间
        ordersMain.setOrderTime(LocalDateTime.now());
        // 结账时间
        ordersMain.setCheckoutTime(LocalDateTime.now());
        // 支付方式
        ordersMain.setPayMethod(ordersParam.getPayMethod());
        // 订单总价值
        ordersMain.setAmount(new BigDecimal(totalPrice));
        // 订单备注
        ordersMain.setRemark(ordersParam.getRemark());
        // 当前登录人的名称
        User user = this.userService.getById(BaseTreadlock.getCurrentId());
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
        this.iOrderDetailService.saveBatch(orderDetailList);
        // 8.删除购物车数据
        LambdaQueryWrapper<ShoppingCart> removeQueryWrapper = new LambdaQueryWrapper<>();
        // 当前登录用户删除当前登录用户的购物车
        shoppingCartWrapper.eq(ShoppingCart::getUserId, BaseTreadlock.getCurrentId());
        this.iShoppingCartService.remove(removeQueryWrapper);
    }

    /**
     * 服务端分页
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Page<Orders> getPage(Integer page, Integer pageSize, String number, String beginTime, String endTime) {

        Page<Orders> ordersPage=new Page<>();
        ordersPage.setSize(pageSize);
        ordersPage.setCurrent(page);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(number),Orders::getNumber,number);
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            wrapper.between(Orders::getOrderTime, beginTime, endTime);
        }

        return this.page(ordersPage, wrapper);
    }

    /**
     * 查看c端订单
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrderdsDto> getUserPage(Integer page, Integer pageSize) {
        Page<Orders> queryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseTreadlock.getCurrentId());
        Page<Orders> ordersPage = this.page(queryPage, queryWrapper);
        Page<OrderdsDto> result = new Page<>(page, pageSize);
        // 没有查询到记录的话,直接返回
        if (CollectionUtils.isEmpty(ordersPage.getRecords())) {
            return result;
        }
        BeanUtils.copyProperties(ordersPage, result, "records");
        List<OrderdsDto> orderDtoList = ordersPage.getRecords().stream().map(item -> {
            OrderdsDto dto = new OrderdsDto();
            BeanUtils.copyProperties(item, dto);
            // 查询子订单信息
            LambdaQueryWrapper<OrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
            orderDetailWrapper.eq(OrderDetail::getOrderId, item.getId());
            // 子订单数据
            List<OrderDetail> orderDetails = this.iOrderDetailService.list(orderDetailWrapper);
            dto.setOrderDetails(orderDetails);
            return dto;
        }).collect(Collectors.toList());
        result.setRecords(orderDtoList);
        return result;
    }

    /**
     * 再来一单
     * @param orders
     * @return
     */
    @Override
    public boolean againOrder(Orders orders) {
        Optional.ofNullable(orders.getId())
                .orElseThrow(() -> new CustomException(AntPathmathcherSS.FAILED));
        Orders oldOrder = this.getById(orders.getId());
        Optional.ofNullable(oldOrder)
                .orElseThrow(() -> new CustomException(AntPathmathcherSS.FAILED));
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orders.getId());
        List<OrderDetail> orderDetailList = this.iOrderDetailService.list(queryWrapper);
        if (CollectionUtils.isEmpty(orderDetailList)) {
            throw new CustomException(AntPathmathcherSS.FAILED + ",请重新选择商品后下单");
        }
        orderDetailList.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id");
            shoppingCart.setUserId(BaseTreadlock.getCurrentId());
            this.iShoppingCartService.save(shoppingCart);
        });
        return Boolean.TRUE;
    }
}
