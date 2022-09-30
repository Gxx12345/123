package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细
 *
 * @author Gmy
 * @since 2022/9/30 16:57
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
