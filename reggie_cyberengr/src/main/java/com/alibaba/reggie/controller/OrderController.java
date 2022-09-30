package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.dto.DishDto;
import com.alibaba.reggie.entity.Category;
import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.entity.Orders;
import com.alibaba.reggie.service.IOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}", orders);
        orderService.submit(orders);
        return Result.success("下单成功");
    }

    @GetMapping("/userPage")
    public Result<Page<Orders>> pageResult(Integer page, Integer pageSize) {
        if (page == null || pageSize == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        Page<Orders> ordersPage = new Page<>();
        ordersPage.setCurrent(page);
        ordersPage.setSize(pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Orders::getCheckoutTime)
                .orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage, lambdaQueryWrapper);
        return Result.success(ordersPage);
    }

    @PostMapping("/again")
    public Result<String> submitAgain(@RequestBody Orders orders) {
        orderService.submit(orders);
        return Result.success("下单成功");
    }
}
