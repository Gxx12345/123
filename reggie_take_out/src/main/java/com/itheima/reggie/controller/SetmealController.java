package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.ICategoryService;
import com.itheima.reggie.service.ISetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐
 *
 * @author my
 * @since 2022/9/29 11:09
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private ISetmealService setmealService;

    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto dto) {
        this.setmealService.saveWithDish(dto);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    @Transactional
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        Page<Setmeal> queryPage = new Page<>();
        queryPage.setCurrent(page);
        queryPage.setSize(pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);
        Page<Setmeal> setmealPage = this.setmealService.page(queryPage, queryWrapper);
        Page<SetmealDto> setmealDto = new Page<>();
        BeanUtils.copyProperties(setmealPage, setmealDto);
        List<SetmealDto> collect = setmealPage.getRecords().stream().map(item -> {
                    SetmealDto dto = new SetmealDto();
                    BeanUtils.copyProperties(item, dto);
                    Category category = this.categoryService.getById(item.getCategoryId());
                    if (category != null) {
                        dto.setCategoryName(category.getName());
                    }
                    return dto;
                }
        ).collect(Collectors.toList());
        setmealDto.setRecords(collect);
        return R.success(setmealDto);
    }

    /**
     * 删除套餐信息
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids ==> {}",ids);
        this.setmealService.removeWithDish(ids);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 起售/停售
     */
    @PostMapping("/status/{status}")
    public R<String> delete(@PathVariable Integer status,@RequestParam List<Long> ids){
        log.info("status => {},ids =>{}",status,ids);
        LambdaUpdateWrapper<Setmeal> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(CollectionUtils.isNotEmpty(ids),Setmeal::getId, ids)
                .set(Setmeal::getStatus, status);
        this.setmealService.update(wrapper);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        log.info("id => {}",id);
        SetmealDto setmealDto = this.setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改
     */
    @PutMapping
    public R<String> put(@RequestBody SetmealDto setmealDto){
        log.info("setmealDto => {}",setmealDto.toString());
        this.setmealService.updateWithDish(setmealDto);
        return R.success(GlobalConstant.FINISH);
    }
}
