package com.itheima.reggie.service.impl;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 地址薄
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

    /**
     * 分页方法
     *
     * @param page      页码
     * @param pageSize  每页记录数
     * @param number    订单号
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @Override
    public Page<Orders> getPage(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        // 创建一个page对象
        // 通过page对象修改值
        Page<Orders> queryPage = new Page<>();
        queryPage.setCurrent(page);
        queryPage.setSize(pageSize);
        //  拼接
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //  通过eq进行比较，首先判断number的值是否是空的，如果是空的后面就不会拼接，不为空再拼接
        queryWrapper.eq(StringUtils.isNotBlank(number), Orders::getNumber, number);
        //  判断beginTime和endTime是否是空，不为空在拼接
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.between(Orders::getOrderTime, beginTime, endTime);
        }
        //  返回最终结果
        return this.page(queryPage, queryWrapper);
    }

    /**
     * 获取用户历史订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrderDto> getUserPage(Integer page, Integer pageSize) {
        //  创建page对象把参数传进去
        Page<Orders> queryPage = new Page<>(page, pageSize);
        //  创建构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //  获取当前登录的用户ID
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentUserId());
        //  根据时间进行降序
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //  把获取的参数传递给新的page对象
        Page<Orders> ordersPage = this.page(queryPage, queryWrapper);
        Page<OrderDto> result = new Page<>(page, pageSize);
        //  没有查到记录的话，直接返回
        if (CollectionUtils.isEmpty(ordersPage.getRecords())) {
            return result;
        }
        //  把数据拷贝到result当中，不考虑records
        BeanUtils.copyProperties(ordersPage, result, "records");
        List<OrderDto> orderDtoList = ordersPage.getRecords().stream().map(item -> {
            OrderDto dto = new OrderDto();
            BeanUtils.copyProperties(item,dto);
            //  查询子订单
            LambdaQueryWrapper<OrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
            orderDetailWrapper.eq(OrderDetail::getOrderId, item.getId());
            //  子订单数据
            List<OrderDetail> orderDetails = this.orderDetailService.list(orderDetailWrapper);
            dto.setOrderDetails(orderDetails);
            return dto;
        }).collect(Collectors.toList());
        result.setRecords(orderDtoList);
        return result;
    }

    /**
     * 再来一单
     *
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public boolean againOrder(Orders orders) {
        Optional.ofNullable(orders.getId())
                .orElseThrow(() -> new CustomException(GlobalConstant.ERROR_PARAM));
        Orders oldOrder = this.getById(orders.getId());
        Optional.ofNullable(oldOrder)
                .orElseThrow(() -> new CustomException(GlobalConstant.ERROR_PARAM));
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orders.getId());
        List<OrderDetail> orderDetailList = this.orderDetailService.list(queryWrapper);
        if (CollectionUtils.isEmpty(orderDetailList)) {
            throw new CustomException(GlobalConstant.ERROR_PARAM + ",请重新选择商品下单");
        }
        orderDetailList.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart,"id");
            shoppingCart.setUserId(BaseContext.getCurrentUserId());
            this.shoppingCartService.add(shoppingCart);
        });
        return Boolean.TRUE;
    }
}
