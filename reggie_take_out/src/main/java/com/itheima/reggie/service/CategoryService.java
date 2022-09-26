package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * 业务层接口
 *
 * @author Gmy
 * @since 2022/9/26 12:06
 */
public interface CategoryService extends IService<Category> {
    /**
     * 自定义删除方法
     */
    void deleteById(Long id);
}
