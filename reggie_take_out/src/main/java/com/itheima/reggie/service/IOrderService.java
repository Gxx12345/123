package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

/**
 * 订单
 *
 * @author my
 * @since 2022/9/30 16:57
 */
public interface IOrderService extends IService<Orders> {
    /**
     *用户下单
     * @param orders
     */
    void submit(Orders orders);
}
