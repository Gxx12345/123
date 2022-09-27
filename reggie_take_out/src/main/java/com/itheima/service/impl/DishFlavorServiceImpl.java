package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.DishFlavor;
import com.itheima.mapper.DishFlavorMapper;
import com.itheima.service.IDishFlavorService;
import org.springframework.stereotype.Service;

/**
 * 口味的业务层
 *
 * @author L
 * @since 2022/9/27 14:59
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements IDishFlavorService {
}
