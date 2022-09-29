package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.common.CustomException;
import com.alibaba.reggie.dto.SetmealDto;
import com.alibaba.reggie.entity.Category;
import com.alibaba.reggie.entity.Setmeal;
import com.alibaba.reggie.entity.SetmealDish;
import com.alibaba.reggie.mapper.SetmealMapper;
import com.alibaba.reggie.service.ICategoryService;
import com.alibaba.reggie.service.ISetmealDishService;
import com.alibaba.reggie.service.ISetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 套餐
 *
 * @author cyberengr
 * @since 2022/9/26 16:44
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {

    @Autowired
    private ISetmealDishService setmealDishService;
    @Autowired
    private ICategoryService categoryService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        save(setmeal);
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        if (list.size() > 0) {
            list.forEach(item -> item.setSetmealId(setmeal.getId()));
            setmealDishService.saveBatch(list);
        }
    }

    @Override
    public Page<SetmealDto> getPage(Long page, Long pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>();
        setmealPage.setCurrent(page);
        setmealPage.setSize(pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);
        this.page(setmealPage, queryWrapper);
        Page<SetmealDto> result = new Page<>();
        BeanUtils.copyProperties(setmealPage, result, "records");
        if (setmealPage.getRecords().size() == 0) {
            return result;
        }
        List<SetmealDto> list = new ArrayList<>();
        setmealPage.getRecords().forEach(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            list.add(setmealDto);
        });
        result.setRecords(list);
        return result;
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(ids),Setmeal::getId, ids)
                .eq(Setmeal::getStatus, 1);
        if (count(queryWrapper) > 0) {
            throw new CustomException("套餐正在售卖中，不能删除!");
        }
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(wrapper);
        this.removeByIds(ids);
    }
}
