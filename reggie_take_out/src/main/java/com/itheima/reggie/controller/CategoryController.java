package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类Controller控制层
 *
 * @author yjiiie6
 * @since 2022/9/26 12:03
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加菜品及套餐分类
     *
     * @param categoryParam
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category categoryParam) {

        //isEmpty()内的值为" " 返回false ， 而isBlank()内的值为" " 返回true
        // 前端传来的参数都是不可信的 ， 故需要判断值是否符合
        // 如果传入的name为null或""  则返回error --- 校验分类名称
        if (StringUtils.isBlank(categoryParam.getName())) {
            return R.error(GlobalConstant.FAILED);
        }

        // 如果传入的sort为null (sort为Integer类型，故不需要判断是否为"")  则返回error --- 校验分类排序
        if (categoryParam.getSort() == null) {
            return R.error(GlobalConstant.FAILED);
        }

        // 如果传入的type为null (type为Integer类型，故不需要判断是否为"")  则返回error --- 校验分类类型
        if (categoryParam.getType() == null) {
            return R.error(GlobalConstant.FAILED);
        }

        // 判断是否保存成功
        boolean isFinish = iCategoryService.save(categoryParam);
        return isFinish ? R.success(GlobalConstant.FINISH) : R.error(GlobalConstant.FAILED);
    }


    /**
     * 分页查询
     *
     * @param page     当前查询页码
     * @param pageSize 每页展示记录数
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(Integer page, Integer pageSize) {
        //构造分页构造器
        Page<Category> categoryPage = new Page<>(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //按排序升序排序
        queryWrapper.orderByAsc(Category::getSort);

        //执行查询
        Page<Category> result = iCategoryService.page(categoryPage, queryWrapper);
        return R.success(result);
    }


    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        // 调用service业务层判断当前菜品或套餐内是否包含其他菜品
        iCategoryService.remove(id);
        return R.success(GlobalConstant.FINISH);
    }



    /**
     * 根据id修改分类信息
     * @param categoryParam
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category categoryParam) {
        iCategoryService.updateById(categoryParam);
        return R.success(GlobalConstant.FINISH);
    }


}
