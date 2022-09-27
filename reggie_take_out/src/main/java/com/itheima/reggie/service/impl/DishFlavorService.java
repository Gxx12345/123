package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @author Gmy
 * @since 2022/9/27 15:47
 */
@Service
public class DishFlavorService extends ServiceImpl<DishFlavorMapper,DishFlavor> implements com.itheima.reggie.service.DishFlavorService {
}
