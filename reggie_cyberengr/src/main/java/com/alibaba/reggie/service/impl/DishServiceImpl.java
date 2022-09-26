package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.mapper.DishMapper;
import com.alibaba.reggie.service.IDishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 菜品
 *
 * @author cyberengr
 * @since 2022/9/26 16:42
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
}
