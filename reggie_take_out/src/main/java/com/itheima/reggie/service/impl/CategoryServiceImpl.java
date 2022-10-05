package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.ICategoryService;
import com.itheima.reggie.service.IDishService;
import com.itheima.reggie.service.ISetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分类Service业务层
 *
 * @author yjiiie6
 * @since 2022/9/26 12:07
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Autowired
    private IDishService iDishService;

    @Autowired
    private ISetmealService iSetmealService;


    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {

        // 调用父类service中的根据id获取分类的方法
        Category category = getById(id);

        if (category == null) {
            // 如果根据id没有查询到对应的分类数据,那后面的代码都不需要执行了.抛出异常.
            throw new CustomException("传入的参数有误");
        }

        // 根据类型判断,这个分类是菜品,还是套餐.
        switch (category.getType()) {
            // 1 菜品分类 2 套餐分类
            // 菜品分类
            case 1:
                LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
                dishLambdaQueryWrapper.eq(Dish::getCategoryId, category.getId());
                // 返回菜品数量
                int countDish = iDishService.count(dishLambdaQueryWrapper);
                // 分类下存在菜品,抛出业务异常,不允许删除
                if (countDish > 0) {
                    throw new CustomException("菜品分类下还存在数据,不允许删除");
                }
                break;
            // 套餐分类
            case 2:
                LambdaQueryWrapper<Setmeal> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
                setmealDishLambdaQueryWrapper.eq(Setmeal::getCategoryId, category.getId());
                // 返回菜品数量
                int countSetmeal = iSetmealService.count(setmealDishLambdaQueryWrapper);
                if (countSetmeal > 0) {
                    // 分类下存在套菜,抛出业务异常,不允许删除
                    throw new CustomException("套餐分类下还存在数据,不允许删除");
                }
                break;

            default:
                throw new CustomException("您传入的参数有误");
        }

        // 如果没有抛出异常,就代表菜品或者套餐下没有菜品数据,就可以删除.
        removeById(id);

    }
}
