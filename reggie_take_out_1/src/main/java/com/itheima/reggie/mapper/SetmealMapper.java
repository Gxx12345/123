package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐持久层
 *
 * @author t3rik
 * @since 2022/9/26 16:41
 */
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
}
