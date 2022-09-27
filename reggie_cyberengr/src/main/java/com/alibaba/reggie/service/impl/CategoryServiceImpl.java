package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.entity.Category;
import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.entity.Setmeal;
import com.alibaba.reggie.common.CustomException;
import com.alibaba.reggie.mapper.CategoryMapper;
import com.alibaba.reggie.service.ICategoryService;
import com.alibaba.reggie.service.IDishService;
import com.alibaba.reggie.service.ISetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分类管理
 *
 * @author cyberengr
 * @since 2022/9/26 12:06
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    @Autowired
    private IDishService dishService;
    @Autowired
    private ISetmealService setmealService;

    @Override
    public void remove(Long id) {
        Category category = this.getById(id);
        if (category == null) {
            throw new CustomException("传入参数错误");
        }
        Integer type = category.getType();
        switch (type) {
            case 1:
                LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
                dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
                if (dishService.count(dishLambdaQueryWrapper) > 0) {
                    throw new CustomException("该分类下还有菜品");
                }
                break;
            case 2:
                LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
                setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
                if (setmealService.count(setmealLambdaQueryWrapper) > 0) {
                    throw new CustomException("该分类下还有套餐");
                }
                break;
        }
        removeById(id);
    }
}
