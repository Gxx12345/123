package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.entity.OrderDetail;
import com.alibaba.reggie.mapper.OrderDetailMapper;
import com.alibaba.reggie.service.IOrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * OrderDetailServiceImpl
 *
 * @author cyberengr
 * @since 2022/9/30 10:21
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {
}
