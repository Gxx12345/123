package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.entity.DishFlavor;
import com.itheima.ruji.mapper.IDishFlaorMapper;
import com.itheima.ruji.service.IDishFlavorService;
import org.springframework.stereotype.Service;

/**
 * 口味业务层实体类
 *
 * @author Gzz
 * @since 2022/9/27 14:54
 */

@Service
public class DishFlavirServiceImpl extends ServiceImpl<IDishFlaorMapper, DishFlavor>implements IDishFlavorService {
}
