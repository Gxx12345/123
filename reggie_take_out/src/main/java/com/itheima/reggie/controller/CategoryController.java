package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制层
 *
 * @author my
 * @since 2022/9/26 12:03
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加分类
     * @param categoryParam
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category categoryParam){
        log.info("category:{}",categoryParam);
        //分类名称校验
        if (StringUtils.isBlank(categoryParam.getName())){
            return R.error(GlobalConstant.FAILED);
        }
        //排序的校验
        if(categoryParam.getSort() == null){
            return R.error(GlobalConstant.FAILED);
        }
        //分类类型校验
        if(categoryParam.getType() == null){
            return R.error(GlobalConstant.FAILED);
        }
        //成功返回true
        //失败返回false
        boolean isFinish = this.categoryService.save(categoryParam);
        return isFinish ? R.success(GlobalConstant.FINISH):R.error(GlobalConstant.FAILED);
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(Integer page, Integer pageSize){
        log.info("分页查询前后端联通");
        Page<Category> queryPage = new Page<>();
        queryPage.setCurrent(page);
        queryPage.setSize(pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //根据sort升序排列
        queryWrapper.orderByDesc(Category::getSort);
        Page<Category> categoryPage = this.categoryService.page(queryPage, queryWrapper);
        return R.success(categoryPage);
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("id ===> {}",id);

        //调用自定义删除方法
        this.categoryService.deleteById(id);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 修改分类
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        this.categoryService.updateById(category);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 新增菜品时查询分类列表
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = this.categoryService.list(queryWrapper);
        return R.success(list);
    }
}