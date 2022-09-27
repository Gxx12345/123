package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * 分类业务层
 *
 * @author my
 * @since 2022/9/26 12:06
 */
public interface ICategoryService extends IService<Category> {
    /**
     * 自定义删除方法
     * @param id 分类ID
     */
    void deleteById(Long id);
}
