package com.alibaba.reggie.service;

import com.alibaba.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * OrderService
 *
 * @author cyberengr
 * @since 2022/9/30 10:20
 */
public interface IOrderService extends IService<Orders> {
    void submit(Orders orders);
}
