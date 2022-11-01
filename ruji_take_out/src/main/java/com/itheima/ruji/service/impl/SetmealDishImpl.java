package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.entity.SetmealDish;
import com.itheima.ruji.mapper.ISetmealDishMapper;
import com.itheima.ruji.service.ISetmealDishService;
import org.springframework.stereotype.Service;

/**
 * 套餐菜品关系实体类
 *
 * @author Gzz
 * @since 2022/9/29 11:10
 */

@Service
public class SetmealDishImpl extends ServiceImpl<ISetmealDishMapper, SetmealDish>implements ISetmealDishService {
}
