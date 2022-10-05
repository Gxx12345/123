package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.IOrderDetailService;
import com.itheima.reggie.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 *
 * @author my
 * @since 2022/9/30 17:04
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        this.orderService.submit(orders);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 订单明细
     *
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(Integer page, Integer pageSize, @RequestParam(required = false) Long number, @RequestParam(required = false) LocalDateTime beginTime, @RequestParam(required = false) LocalDateTime endTime) {
        if (page == null || pageSize == null) {
            return R.error(GlobalConstant.FAILED);
        }
        Page<Orders> ordersPage = new Page<>();
        ordersPage.setCurrent(page);
        ordersPage.setSize(pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(number != null, Orders::getNumber, number)
                .between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime)
                .orderByDesc(Orders::getCheckoutTime)
                .orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage, lambdaQueryWrapper);
        return R.success(ordersPage);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<OrderDto>> pageResult(Integer page, Integer pageSize) {
        if (page == null || pageSize == null) {
            return R.error(GlobalConstant.FAILED);
        }
        Page<Orders> ordersPage = new Page<>();
        ordersPage.setCurrent(page);
        ordersPage.setSize(pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId())
                .orderByDesc(Orders::getCheckoutTime)
                .orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage, lambdaQueryWrapper);
        Page<OrderDto> result = new Page<>(page, pageSize);
        // 没有查询到记录的话,直接返回
        if (CollectionUtils.isEmpty(ordersPage.getRecords())) {
            return R.success(result);
        }
        BeanUtils.copyProperties(ordersPage, result, "records");
        List<OrderDto> orderDtoList = ordersPage.getRecords().stream().map(item -> {
            OrderDto dto = new OrderDto();
            BeanUtils.copyProperties(item, dto);
            // 查询子订单信息
            LambdaQueryWrapper<OrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
            orderDetailWrapper.eq(OrderDetail::getOrderId, item.getId());
            // 子订单数据
            List<OrderDetail> orderDetails = this.orderDetailService.list(orderDetailWrapper);
            dto.setOrderDetails(orderDetails);
            return dto;
        }).collect(Collectors.toList());
        result.setRecords(orderDtoList);
        return R.success(result);
    }

    /**
     * 派送订单
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> send(@RequestBody Orders orders) {
        if (orders.getId() != null || orders.getStatus() != null) {
            return R.error(GlobalConstant.FAILED);
        }
        LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper();
        wrapper.eq(Orders::getId,orders.getId())
                .set(Orders::getStatus,orders.getStatus());
        orderService.update(wrapper);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 再次订单
     *
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> submitAgain(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("下单成功");
    }
}
