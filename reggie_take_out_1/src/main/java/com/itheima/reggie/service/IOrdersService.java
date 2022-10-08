package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

/**
 * 订单业务层
 *
 * @author t3rik
 * @since 2022/9/30 15:46
 */
public interface IOrdersService extends IService<Orders> {
    /**
     * 下单
     * @param orders
     */
    void submit(Orders orders);
}
