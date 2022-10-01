package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
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

/**
 * 套餐
 *
 * @author my
 * @since 2022/9/26 17:41
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {
    @Autowired
    private ISetmealDishService setmealDishService;
    /**
     * 添加套餐
     * @param dto
     */
    @Override
    public void saveWithDish(SetmealDto dto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto,setmeal);
        this.save(setmeal);
        Long setmealId = setmeal.getId();
        if(CollectionUtils.isNotEmpty(dto.getSetmealDishes())){
            for (SetmealDish item : dto.getSetmealDishes()) {
                item.setSetmealId(setmealId);
            }
            this.setmealDishService.saveBatch(dto.getSetmealDishes());
        }
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if(count!=0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        this.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        this.setmealDishService.remove(lambdaQueryWrapper);
    }
}
