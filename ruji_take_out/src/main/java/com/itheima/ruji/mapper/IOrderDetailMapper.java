package com.itheima.ruji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.ruji.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细持久层
 *
 * @autho Gzz
 * @since 2022/9/30 17:14
 */
@Mapper
public interface IOrderDetailMapper extends BaseMapper<OrderDetail> {
}
