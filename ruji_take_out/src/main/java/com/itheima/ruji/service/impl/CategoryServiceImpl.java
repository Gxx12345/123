package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.common.CustomException;
import com.itheima.ruji.entity.Category;
import com.itheima.ruji.entity.Dish;
import com.itheima.ruji.entity.Setmeal;
import com.itheima.ruji.mapper.ICategoryMapper;
import com.itheima.ruji.service.ICategoryService;
import com.itheima.ruji.service.IDishService;
import com.itheima.ruji.service.ISetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分类业务层实现类
 *
 * @author t3rik
 * @since 2022/9/26 11:47
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<ICategoryMapper, Category> implements ICategoryService {

    /**
     * 菜品
     */
    @Autowired
    private IDishService dishService;
    /**
     * 套餐
     */
    @Autowired
    private ISetmealService setmealService;

    /**
     * 自定义删除方法
     *
     * @param id 分类ID
     */
    @Override
    public void deleteByid(Long id) {
        // 1. 根据这个id,先查询这个分类
        // 根据分类ID查询分类
        // 返回的是分类
        Category category = getById(id);
        if (category == null) {
            throw new CustomException("传入的参数有误");
        }
        // 2. 根据这个分类的类型,来判断要查询的是菜品还是套餐
        // 分类类型 1 菜品 2 套餐
        Integer type = category.getType();
        // 3. 进入到判断的逻辑,如果这个分类下还存在菜品或者是套餐的话,那么就不允许删除
        switch (type) {
            // 菜品的处理
            case 1:
                // 根据分类的id,查询菜品(dish)表.
                LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
                // 根据分类id查询
                dishQueryWrapper.eq(Dish::getCategoryId, category.getId());
                // 调用dish的service去查询了
                // 看一下菜品表中是否有这个id的相关的数据
                // select count(*) from Dish where category_id = ''
                int dishCount = this.dishService.count(dishQueryWrapper);
                // 如果有,就不允许删除
                if (dishCount > 0) {
                    throw new CustomException("该分类下有菜品数据,不允许删除");
                }
                break;
            // 套餐的处理
            case 2:
                LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
                // 根据分类id查询套餐
                setmealQueryWrapper.eq(Setmeal::getCategoryId, category.getId());
                int setmealCount = this.setmealService.count(setmealQueryWrapper);
                if (setmealCount > 0) {
                    throw new CustomException("该分类下有套餐数据,不允许删除");
                }
                break;
            default:
                throw new CustomException("传入的参数有误");
        }
        // 4. 这个分类下没有菜品或者是套餐,就调用删除方法
        removeById(category.getId());
    }
}
