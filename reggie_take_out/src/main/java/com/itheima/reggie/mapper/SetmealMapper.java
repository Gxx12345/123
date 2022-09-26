package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐持久层
 *
 * @author Gmy
 * @since 2022/9/26 17:56
 */
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
}
