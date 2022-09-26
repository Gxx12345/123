package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * 分类Service业务层接口
 *
 * @author yjiiie6
 * @since 2022/9/26 12:05
 */
public interface ICategoryService extends IService<Category> {
    void remove(Long id);
}
