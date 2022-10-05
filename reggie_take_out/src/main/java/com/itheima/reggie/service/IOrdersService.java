package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;

import java.util.List;

/**
 * 订单业务层
 *
 * @author yjiiie6
 * @since 2022/10/1 10:34
 */
public interface IOrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     * @return
     */
    void submit(Orders orders);


    /**
     * 订单明细分页查询
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    Page<Orders> getPage(Integer page, Integer pageSize, String number, String beginTime, String endTime);


    /**
     * 再来一单
     * @param orders
     * @return
     */
    boolean againOrder(Orders orders);


    /**
     * 个人中心 历史订单
     * @param page
     * @param pageSize
     * @return
     */
    Page<OrdersDto> getUserPage(Integer page, Integer pageSize);
}
