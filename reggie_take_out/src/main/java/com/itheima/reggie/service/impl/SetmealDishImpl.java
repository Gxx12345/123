package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.service.ISetmealDishService;
import org.springframework.stereotype.Service;

/**
 * 套餐和菜品关系表
 *
 * @author yjiiie6
 * @since 2022/9/29 11:13
 */
@Service
public class SetmealDishImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements ISetmealDishService {
}
