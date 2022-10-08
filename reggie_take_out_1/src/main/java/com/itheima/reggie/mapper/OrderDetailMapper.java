package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细
 *
 * @author t3rik
 * @since 2022/9/30 15:49
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
