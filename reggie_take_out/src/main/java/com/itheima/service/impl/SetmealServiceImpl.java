package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.Setmeal;
import com.itheima.mapper.ISetmealMapper;
import com.itheima.service.ISetmealService;
import org.springframework.stereotype.Service;

/**
 * 套餐的业务层
 *
 * @author L
 * @since 2022/9/26 18:01
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<ISetmealMapper, Setmeal> implements ISetmealService {
}
