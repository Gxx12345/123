package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单持久层
 *
 * @author yjiiie6
 * @since 2022/10/1 10:33
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
