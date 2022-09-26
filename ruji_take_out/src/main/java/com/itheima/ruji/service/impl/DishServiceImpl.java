package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.entity.Dish;
import com.itheima.ruji.mapper.IDishMapper;
import com.itheima.ruji.service.IDishService;
import org.springframework.stereotype.Service;

/**
 * 菜品业务层实现类
 *
 * @author Gzz
 * @since 2022/9/26 16:54
 */

@Service
public class DishServiceImpl extends ServiceImpl<IDishMapper, Dish>implements IDishService {
}
