package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 套餐控制器
 * @author Gmy
 * @since 2022/9/29 11:27
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> save(@RequestBody SetmealDto dto) {
        log.info("前后端联通");
        //  新增套餐
        //  在service中拓展的方法
        setmealService.saveWithDish(dto);
        return null;
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        //  1. 构建分页条件对象
        Page<Setmeal> queryPage = new Page<>();
        //  当前页
        queryPage.setCurrent(page);
        //  当前页显示多少行
        queryPage.setSize(pageSize);
        //  2. 构建查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);
        //  3. 执行分页查询条件
        Page<Setmeal> setmealPage = setmealService.page(queryPage, queryWrapper);
        //  4. 构建返回结果对象
        Page<SetmealDto> result = new Page<>();
        //  setmealPage -> result
        //  并copy查询结果到该对象中
        BeanUtils.copyProperties(setmealPage,result);
        //  5. 遍历分页查询列表数据
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        for (Setmeal item : setmealPage.getRecords()) {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item,dto);
            //  查询分类
            Category category = this.categoryService.getById(item.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            setmealDtoList.add(dto);
        }
        //  把Setmeal对象转为SetmealDto对象，
        //  同时赋值分类名称
        //  6. 封装数据并返回
        result.setRecords(setmealDtoList);
        return R.success(result);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("前后端联通");
        setmealService.deleteByIds(ids);
        return null;
    }
}
