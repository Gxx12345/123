package com.itheima.ruji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.ruji.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单持久层
 *
 * @autho Gzz
 * @since 2022/9/30 17:13
 */
@Mapper
public interface IOrdersMapper extends BaseMapper<Orders> {
}
