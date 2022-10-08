package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 主表订单
 *
 * @author t3rik
 * @since 2022/9/30 15:45
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
