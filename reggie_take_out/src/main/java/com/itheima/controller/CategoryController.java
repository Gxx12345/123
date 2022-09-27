package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.GlobalConstant;
import com.itheima.common.R;
import com.itheima.entity.Category;
import com.itheima.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 请求响应类 控制层
 *
 * @author L
 * @since 2022/9/26 12:02
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category categoryParam) {
        log.info("categoryParam ==>{}",categoryParam.toString());
        //分类名称的校验
        if (StringUtils.isBlank(categoryParam.getName())) {
            return R.success(GlobalConstant.FAILED);
        }
        //排序的校验
        if (categoryParam.getSort() == null) {
            return R.success(GlobalConstant.FAILED);
        }
        //分类类型的校验
        if (categoryParam.getType() == null) {
            return R.success(GlobalConstant.FAILED);
        }
        //把前端传入的数据，保存到数据库中
        //controller->调用service-> 调用mapper
        //创建时间
        //修改时间
        //因为自动填充
        boolean isFinish = this.categoryService.save(categoryParam);
        return isFinish ? R.success(GlobalConstant.FINISH) : R.success(GlobalConstant.FAILED);
    }

    @GetMapping("/page")
    public R<Page<Category>> page(Integer page,Integer pageSize) {
        log.info("前后端互通");
        //使用mp来完成分页查询
        //分页对象
        Page<Category> queryPage = new Page<>();
        //当前页
        queryPage.setCurrent(page);
        //当前页要显示多少行
        queryPage.setSize(pageSize);
        //排序字段
        //使用mp的话，条件都是wrapper中的
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //根据sort字段进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //调用service中的分页方法，来查询分类的分页数据
        Page<Category> result = this.categoryService.page(queryPage, queryWrapper);
        //调用page方法时，可以使用查询的分页对象
        //也可以新建一个对象，来接收这个返回的结果集
        //this.categoryService.page(queryPage, queryWrapper);
        return R.success(result);
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        log.info("id ===> {}", id);
        // 根据主键id删除数据
        // mp提供的
//        this.categoryService.removeById(id);
        // 我们要调用的是我们自定义的删除方法
        this.categoryService.deleteById(id);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 修改分类
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        // 调用service中的update方法
        this.categoryService.updateById(category);
        return R.success(GlobalConstant.FINISH);
    }
}
