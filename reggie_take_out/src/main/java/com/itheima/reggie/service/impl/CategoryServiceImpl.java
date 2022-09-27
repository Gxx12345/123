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
 * 分类业务层实现类
 *
 * @author my
 * @since 2022/9/26 12:08
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    @Autowired
    private IDishService dishService;

    @Autowired
    private ISetmealService setmealService;

    @Override
    public void deleteById(Long id) {
        //根据这个Id,先查询这个分类
        Category category = this.getById(id);
        if(category == null){
            throw new CustomException("传入的参数有误");
        }
        //根据这个分类的类型,来判断要查询的是菜品还是套餐
        Integer type = category.getType();
        //进入到判断的逻辑，如果这个分类下还存在套餐或菜品，就不允许删除
        switch (type){
            //菜品的处理
            case 1:
                //根据分类的id,查询菜品表
                LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
                dishQueryWrapper.eq(Dish::getCategoryId,category.getId());
                int disCount = this.dishService.count(dishQueryWrapper);
                if(disCount>0){
                    throw new CustomException("该分类下有菜品数据，不允许删除");
                }
                break;
            //套餐的处理
            case 2:
                LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
                setmealQueryWrapper.eq(Setmeal::getCategoryId,category.getId());
                int setmealCount = this.setmealService.count(setmealQueryWrapper);
                if(setmealCount>0){
                    throw new CustomException("该分类下有套餐数据，不允许删除");
                }
                break;
            default:
                throw new CustomException("传入的参数有误");
        }
        //这个分类下没有套餐和菜品，直接删除
        removeById(category.getId());
    }
}
