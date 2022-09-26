package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.ICategoryService;
import com.itheima.reggie.service.IEmployeeService;
import com.itheima.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public R<String> save(@RequestBody Category categoryParam){
        log.info("category:{}",categoryParam);
        this.categoryService.save(categoryParam);
        return R.success("新增分类成功");
    }
}

