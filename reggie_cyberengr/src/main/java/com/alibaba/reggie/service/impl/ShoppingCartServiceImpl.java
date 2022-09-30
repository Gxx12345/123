package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.entity.ShoppingCart;
import com.alibaba.reggie.mapper.ShoppingCartMapper;
import com.alibaba.reggie.service.IShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * ShoppingCartServiceImpl
 *
 * @author cyberengr
 * @since 2022/9/30 10:15
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {
}