package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.Dish;
import com.itheima.mapper.IDishMapper;
import com.itheima.service.IDishService;
import org.springframework.stereotype.Service;

/**
 * 菜品的业务层
 *
 * @author L
 * @since 2022/9/26 17:57
 */
@Service
public class DishServiceImpl extends ServiceImpl<IDishMapper, Dish> implements IDishService {
}
