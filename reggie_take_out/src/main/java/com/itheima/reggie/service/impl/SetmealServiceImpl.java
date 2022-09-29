package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.ISetmealDishService;
import com.itheima.reggie.service.ISetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 套餐菜品业务层
 *
 * @author yjiiie6
 * @since 2022/9/26 17:51
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {

    @Autowired
    private ISetmealDishService iSetmealDishService;


    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存两张表
        // 套餐表
        // 设计,单一原则
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        // 保存套餐表
        save(setmeal);

        // 套餐和菜品关系
        // 判断传入的菜品信息是否不为空
        // 不为空的话才去保存
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        // 保存前要给套餐及套餐的菜品关系赋值
        for (SetmealDish item : setmealDishes) {
            // 套餐id
            item.setSetmealId(setmeal.getId());
        }
        // 保存套餐和菜品的关系
        iSetmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        // 套餐状态  0 停售 1 起售
        queryWrapper.in(Setmeal::getStatus, 1);

        int count = count(queryWrapper);
        if (count > 0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐表中的数据---setmeal
        removeByIds(ids);

        //删除关系表中的数据----setmeal_dish
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        iSetmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}