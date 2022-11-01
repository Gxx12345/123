package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.entity.OrderDetail;
import com.itheima.ruji.mapper.IOrderDetailMapper;
import com.itheima.ruji.service.IOrderDetailService;
import org.springframework.stereotype.Service;

/**
 * 订单明细业务层实体类
 *
 * @author Gzz
 * @since 2022/9/30 17:18
 */

@Service
public class OrderDetailImpl extends ServiceImpl<IOrderDetailMapper, OrderDetail>implements IOrderDetailService {
}
