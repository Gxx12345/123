package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.IDishService;
import org.springframework.stereotype.Service;

/**
 * 菜品业务层
 *
 * @author yjiiie6
 * @since 2022/9/26 17:45
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
}
