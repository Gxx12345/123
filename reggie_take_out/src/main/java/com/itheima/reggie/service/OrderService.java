package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.Orders;

/**
 * 订单业务层接口
 *
 * @author Gmy
 * @since 2022/9/30 16:57
 */
public interface OrderService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);

    Page<Orders> getPage(Integer page, Integer pageSize, String number, String beginTime, String endTime);

    /**
     * 获取用户历史订单
     * @param page
     * @param pageSize
     * @return
     */
    Page<OrderDto> getUserPage(Integer page, Integer pageSize);

    /**
     * 再来一单
     * @param orders
     * @return
     */
    boolean againOrder(Orders orders);
}
