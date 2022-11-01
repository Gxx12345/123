package com.itheima.ruji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.ruji.entity.Category;

/**
 * 分类业务层
 *
 * @autho Gzz
 * @since 2022/9/26 12:05
 */

public interface ICategoryService extends IService<Category> {
    /**
     * 自定义删除方法
     * @param id 分类ID
     */
    void deleteByid(Long id);
}
