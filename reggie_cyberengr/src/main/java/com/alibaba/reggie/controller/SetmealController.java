package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.CustomException;
import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.dto.SetmealDto;
import com.alibaba.reggie.entity.Dish;
import com.alibaba.reggie.entity.Setmeal;
import com.alibaba.reggie.entity.SetmealDish;
import com.alibaba.reggie.service.IDishService;
import com.alibaba.reggie.service.ISetmealDishService;
import com.alibaba.reggie.service.ISetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SetmealController
 *
 * @author cyberengr
 * @since 2022/9/29 9:39
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private ISetmealService setmealService;
    @Autowired
    private ISetmealDishService setmealDishService;
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true) //清除setmealCache名称下,所有的缓存数据
    public Result<String> addSetmealDto(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return Result.success(GlobalConstant.FINISHED);
    }

    @GetMapping("/page")
    public Result<Page<SetmealDto>> pageResult(Long page, Long pageSize, String name) {
        if (page == null || pageSize == null) {
            throw new CustomException("传入参数有误！");
        }
        Page<SetmealDto> dtoPage = setmealService.getPage(page, pageSize, name);
        return Result.success(dtoPage);
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true) //清除setmealCache名称下,所有的缓存数据
    public Result<String> deleteIds(@RequestParam List<Long> ids) {
        setmealService.deleteByIds(ids);
        return Result.success(GlobalConstant.FINISHED);
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(CollectionUtils.isNotEmpty(ids), Setmeal::getId, ids)
                .set(Setmeal::getStatus, status);
        this.setmealService.update(updateWrapper);
        return Result.success(GlobalConstant.FINISHED);
    }

    /**
     * 根据条件查询套餐数据
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
    public Result<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<SetmealDto> getSetmealDto(@PathVariable Long id) {
        if (id == null) {
            return Result.error("传入参数有误!");
        }
        Setmeal setmeal = setmealService.getById(id);
        LambdaQueryWrapper<SetmealDish> wrapper=new LambdaQueryWrapper();
        wrapper.in(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(list);
        return Result.success(setmealDto);
    }
}
