package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.mapper.OrderDetailMapper;
import com.itheima.reggie.service.IOrderDetailService;
import org.springframework.stereotype.Service;

/**
 * 订单明细业务层
 *
 * @author yjiiie6
 * @since 2022/10/1 10:51
 */
@Service
public class OrderDetailImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {
}
