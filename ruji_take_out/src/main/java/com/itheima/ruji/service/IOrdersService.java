package com.itheima.ruji.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.ruji.dto.OrderdsDto;
import com.itheima.ruji.entity.Orders;

import javax.servlet.http.HttpSession;

/**
 * 订单业务层
 *
 * @autho Gzz
 * @since 2022/9/30 17:15
 */
public interface IOrdersService extends IService<Orders> {
    void submit(Orders ordersParam);

    Page<Orders> getPage(Integer page, Integer pageSize, String number, String beginTime, String endTime);

    Page<OrderdsDto> getUserPage(Integer page, Integer pageSize);

    boolean againOrder(Orders orders);

}
