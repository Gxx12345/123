package com.alibaba.reggie.mapper;

import com.alibaba.reggie.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * OrderMapper
 *
 * @author cyberengr
 * @since 2022/9/30 10:19
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}