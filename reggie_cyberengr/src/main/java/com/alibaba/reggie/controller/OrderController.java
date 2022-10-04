package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.BaseContext;
import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.dto.DishDto;
import com.alibaba.reggie.dto.OrderDto;
import com.alibaba.reggie.entity.Category;
import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.entity.OrderDetail;
import com.alibaba.reggie.entity.Orders;
import com.alibaba.reggie.service.IOrderDetailService;
import com.alibaba.reggie.service.IOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 *
 * @author cyberengr
 * @since 2022/9/30 10:22
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
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}", orders);
        orderService.submit(orders);
        return Result.success("下单成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page<OrderDto>> pageResult(Integer page, Integer pageSize) {
        if (page == null || pageSize == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        Page<Orders> ordersPage = new Page<>();
        ordersPage.setCurrent(page);
        ordersPage.setSize(pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId, BaseContext.getSetThreadLocalCurrentId())
                .orderByDesc(Orders::getCheckoutTime)
                .orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage, lambdaQueryWrapper);
        Page<OrderDto> result = new Page<>(page, pageSize);
        // 没有查询到记录的话,直接返回
        if (CollectionUtils.isEmpty(ordersPage.getRecords())) {
            return Result.success(result);
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
        return Result.success(result);
    }

    /**
     * 再次订单
     *
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public Result<String> submitAgain(@RequestBody Orders orders) {
        orderService.submit(orders);
        return Result.success("下单成功");
    }

    /**
     * 订单明细
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public Result<Page<Orders>> page(Integer page, Integer pageSize, @RequestParam(required = false) Long number,@RequestParam(required = false) LocalDateTime beginTime, @RequestParam(required = false) LocalDateTime endTime) {
        if (page == null || pageSize == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        Page<Orders> ordersPage = new Page<>();
        ordersPage.setCurrent(page);
        ordersPage.setSize(pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(number!=null,Orders::getNumber,number)
                .between(beginTime!=null&&endTime!=null,Orders::getOrderTime,beginTime,endTime)
                .orderByDesc(Orders::getCheckoutTime)
                .orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage, lambdaQueryWrapper);
        return Result.success(ordersPage);
    }

    /**
     * 派送订单
     * @param orders
     * @return
     */
    @PutMapping
    public Result<String> send(@RequestBody Orders orders) {
        if (orders.getId() != null || orders.getStatus() != null) {
            return Result.error(GlobalConstant.FAILED);
        }
        LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper();
        wrapper.eq(Orders::getId,orders.getId())
                .set(Orders::getStatus,orders.getStatus());
        orderService.update(wrapper);
        return Result.success(GlobalConstant.FINISHED);
    }
}
