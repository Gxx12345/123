package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.ISetmealService;
import org.springframework.stereotype.Service;

/**
 * 套餐菜品业务层
 *
 * @author yjiiie6
 * @since 2022/9/26 17:51
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {
}
