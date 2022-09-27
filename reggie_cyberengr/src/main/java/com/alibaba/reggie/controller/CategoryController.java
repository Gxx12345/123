package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.entity.Category;
import com.alibaba.reggie.service.ICategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 *
 * @author cyberengr
 * @since 2022/9/26 12:08
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private ICategoryService service;

    /**
     * 新增分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> addCategory(@RequestBody Category category) {
        if (StringUtils.isBlank(category.getName()) || category.getType() == null || category.getSort() == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        boolean save = service.save(category);
        return save ? Result.success(GlobalConstant.FINISHED) : Result.error(GlobalConstant.FAILED);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page<Category>> pageResult(Integer page, Integer pageSize) {
        if (page == null || pageSize == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        Page<Category> categoryPage = new Page<>();
        categoryPage.setCurrent(page);
        categoryPage.setSize(pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper();
        wrapper.orderByAsc(Category::getSort);
        service.page(categoryPage, wrapper);
        return Result.success(categoryPage);
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long id) {
        if (id == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        service.remove(id);
        return Result.success(GlobalConstant.FINISHED);
    }

    /**
     * 更新分类
     * @param category
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        if (category.getId() == null) {
            return Result.error("传入的参数错误!");
        }
        if (service.getById(category) != null) {
            boolean update = service.updateById(category);
            return update ? Result.success(GlobalConstant.FINISHED) : Result.error(GlobalConstant.FAILED);
        }
        return Result.error(GlobalConstant.FAILED);
    }

    /**
     * 查询分类列表
     * @param categoryParam
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Category categoryParam) {
        Integer type = categoryParam.getType();
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(type != null, Category::getType, type)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);
        List<Category> list = service.list(queryWrapper);
        return Result.success(list);
    }
}
