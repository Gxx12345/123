package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.common.BaseTreadlock;
import com.itheima.ruji.common.CustomException;
import com.itheima.ruji.entity.ShoppingCart;
import com.itheima.ruji.mapper.IShoppingCartMapper;
import com.itheima.ruji.service.IShoppingCartService;
import com.sun.prism.impl.BaseContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 购物车业务层实体类
 *
 * @author Gzz
 * @since 2022/9/30 12:21
 */

@Service
public class ShoppingCartImpl extends ServiceImpl<IShoppingCartMapper, ShoppingCart>implements IShoppingCartService {

    }

