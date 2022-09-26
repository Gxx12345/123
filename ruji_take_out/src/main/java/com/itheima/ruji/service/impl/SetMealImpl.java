package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.entity.Setmeal;
import com.itheima.ruji.mapper.ISetmealMapper;
import com.itheima.ruji.service.ISetmealService;
import org.springframework.stereotype.Service;

/**
 * 套餐业务层实现类
 *
 * @author Gzz
 * @since 2022/9/26 17:47
 */

@Service
public class SetMealImpl extends ServiceImpl<ISetmealMapper, Setmeal>implements ISetmealService {
}
