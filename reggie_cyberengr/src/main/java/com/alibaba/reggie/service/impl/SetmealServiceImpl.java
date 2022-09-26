package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.entity.Setmeal;
import com.alibaba.reggie.mapper.SetmealMapper;
import com.alibaba.reggie.service.ISetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 套餐
 *
 * @author cyberengr
 * @since 2022/9/26 16:44
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {
}
