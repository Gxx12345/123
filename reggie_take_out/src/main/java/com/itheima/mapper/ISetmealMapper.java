package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐的持久层
 *
 * @author L
 * @since 2022/9/26 17:59
 */
@Mapper
public interface ISetmealMapper extends BaseMapper<Setmeal> {
}
