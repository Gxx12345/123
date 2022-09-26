package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.entity.Category;
import com.alibaba.reggie.mapper.CategoryMapper;
import com.alibaba.reggie.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 分类管理
 *
 * @author cyberengr
 * @since 2022/9/26 12:06
 */
@Service
public class CategoryImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
}
