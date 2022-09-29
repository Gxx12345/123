package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务层
 *
 * @author Gmy
 * @since 2022/9/26 18:01
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {

    /**
     * 套餐和菜品关系
     */
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐
     * @param dto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto dto) {
        /*Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto,setmeal);
        this.save(setmeal);
        Long setmealId = setmeal.getId();
        if (CollectionUtils.isNotEmpty(dto.getSetmealDishes())) {
            for (SetmealDish item : dto.getSetmealDishes()) {
                item.setSetmealId(setmealId);
            }
            setmealDishService.saveBatch(dto.getSetmealDishes());
        }*/

        //  保存两张表
        //  套餐表
        //  设计，单一原则
        Setmeal setmeal = new Setmeal();
        //  dto -> setmeal
        BeanUtils.copyProperties(dto,setmeal);
        //  保存套餐表
        this.save(setmeal);
        Long setmealId = setmeal.getId();
        //  套餐和菜品关系
        //  判断传入的菜品信息是否不为空
        //  不为空的话采取保存
        if (CollectionUtils.isNotEmpty(dto.getSetmealDishes())) {
            //  保存前要给套餐及套餐的菜品关系赋值
            for (SetmealDish item : dto.getSetmealDishes()) {
                //  套餐id
                item.setSetmealId(setmealId);
            }
            //  保存套餐和菜品的关系
            setmealDishService.saveBatch(dto.getSetmealDishes());
        }
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    public void deleteByIds(List<Long> ids) {
        //  1. 先判断是否有正在售卖的套餐
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //  in (" ", " ")
        queryWrapper.in(Setmeal::getCategoryId, ids);
        // 售卖状态 0 禁售  1 在售
        queryWrapper.in(Setmeal::getStatus, 1);
        //  根据条件查询内容
        //  返回的是否符合条件的数量
        int count = this.count(queryWrapper);
        //  数量如果大于0，就代表本次传进来的有正在售卖的套餐，不运输删除
        if (count > 0) {
            throw new CustomException("套餐正在售卖，无法删除");
        }
        //  2. 删除套餐表
        this.removeByIds(ids);
        //  3. 删除套餐和菜品的关系表
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        this.setmealDishService.remove(lambdaQueryWrapper);
    }
}
