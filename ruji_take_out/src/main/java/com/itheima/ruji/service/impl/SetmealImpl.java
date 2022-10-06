package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.common.CustomException;
import com.itheima.ruji.common.R;
import com.itheima.ruji.dto.SetmealDto;
import com.itheima.ruji.entity.Dish;
import com.itheima.ruji.entity.Setmeal;
import com.itheima.ruji.entity.SetmealDish;
import com.itheima.ruji.mapper.ISetmealMapper;
import com.itheima.ruji.service.IDishService;
import com.itheima.ruji.service.ISetmealDishService;
import com.itheima.ruji.service.ISetmealService;
import jdk.nashorn.internal.runtime.GlobalConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 套餐业务层实现类
 *
 * @author Gzz
 * @since 2022/9/26 17:47
 */

@Service
public class SetmealImpl extends ServiceImpl<ISetmealMapper, Setmeal>implements ISetmealService {
@Autowired
    private ISetmealDishService dishService;
@Autowired
  private ISetmealService iSetmealService;
@Autowired
 private ISetmealDishService iSetmealDishService;


    /**
     * 新增套餐
     *
     * @param dto
     */
    @Override
    @Transactional
    public void saveSetmeal(SetmealDto dto) {
        //保存俩张表
        //套餐表
        Setmeal setmeal=new Setmeal();
        // dto ---->setmeal
        BeanUtils.copyProperties(dto,setmeal);
        //将套餐保存
        save(setmeal);
        Long id = setmeal.getId();
        //套餐和菜品关系
        //判断传入的菜品信息是否为空
        //不为空的话才去保存
        if (CollectionUtils.isNotEmpty(dto.getSetmealDishes())){
            //保存前要给套餐及套餐的菜品关系赋值
            for (SetmealDish setmealDish : dto.getSetmealDishes()) {
                //套餐ID
                setmealDish.setSetmealId(id);
            }
            //保存套餐和菜品信息
            dishService.saveBatch(dto.getSetmealDishes());
        }
    }
    /**
     * 删除套餐
     *
     * @param ids
     */
@Transactional
    @Override
    public void deleteSetmeals(List<Long> ids) {
    // 1. 先判断是否有正在售卖的套餐
    LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
    // in ('','','')
    wrapper.in(Setmeal::getId,ids);
    // 售卖状态 0 禁售  1 在售
    wrapper.eq(Setmeal::getStatus,1);
    // 根据条件查询内容
    // 返回的是符合条件的数量
    // select count(*) from setmeal where id in ('','','') and status = 1
    int count = iSetmealService.count(wrapper);
    // 数量如果大于0,就代表本次传进来的有正在售卖的套餐,不允许删除
    if (count>0){
        throw  new CustomException("商品正在售卖,禁止删除");
    }
    // 2. 删除套餐表
    // delete from setmeal where in ('','','')
    this.removeByIds(ids);
    LambdaQueryWrapper<SetmealDish> wrapperSetmealDish = new LambdaQueryWrapper<>();
    // delete from setmealdish where setmeal_id in ('','','')
    wrapperSetmealDish.in(SetmealDish::getSetmealId,ids);
    iSetmealDishService.remove(wrapperSetmealDish);
}

    /**
     * 根据id查询回调
     * @param id
     * @return
     */
    @Override
    public SetmealDto getSetmealId(Long id) {
        Setmeal byIdSetmeal = getById(id);
        if (byIdSetmeal==null){
            throw new CustomException("传入数据有误");
        }
        SetmealDto dto=new SetmealDto();
        BeanUtils.copyProperties(byIdSetmeal,dto);
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = dishService.list(wrapper);
        dto.setSetmealDishes(list);
        return dto;
    }

    /**
     * 修改
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateDishSetmeal(SetmealDto setmealDto) {
        // 校验
        Setmeal setmeal = this.getById(setmealDto.getId());
        // 更新
        Setmeal update = new Setmeal();
        BeanUtils.copyProperties(setmealDto, update);
        updateById(update);
        if (CollectionUtils.isNotEmpty(setmealDto.getSetmealDishes())) {
            // 先删除
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
            this.iSetmealDishService.remove(queryWrapper);
            // 后添加
            setmealDto.getSetmealDishes().forEach(item -> item.setSetmealId(setmeal.getId()));
            this.iSetmealDishService.saveBatch(setmealDto.getSetmealDishes());
        }
    }
}
