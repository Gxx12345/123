package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Category;

/**
 * 分类
 *
 * @author L
 * @since 2022/9/26 12:05
 */
public interface ICategoryService extends IService<Category> {
    public void deleteById(Long id);
}
