package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 *
 * @author Gmy
 * @since 2022/9/30 16:55
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
