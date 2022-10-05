package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细持久层
 *
 * @author yjiiie6
 * @since 2022/10/1 10:50
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
