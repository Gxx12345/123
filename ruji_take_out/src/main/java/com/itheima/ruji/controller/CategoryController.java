package com.itheima.ruji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.R;
import com.itheima.ruji.entity.Category;
import com.itheima.ruji.service.ICategoryService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制层
 *
 * @author Gzz
 * @since 2022/9/26 12:07
 */

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加分类类型
     * @param categoryParam
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category categoryParam) {
        log.info("前后端联通:{}", categoryParam.toString());
//        if(categoryParam.getName()==null||" ".equals(categoryParam.getName())){
//            R.error(AntPathmathcherSS.FAILED);
//        }
        //分类名称
        if (StringUtils.isBlank(categoryParam.getName())) {
          return    R.error(AntPathmathcherSS.FAILED);
        }
        //排序效验
        if (categoryParam.getType() == null) {
            return  R.error(AntPathmathcherSS.FAILED);
        }
        //分类类型效验
        if (categoryParam.getSort() == null) {
            return   R.error(AntPathmathcherSS.FAILED);
        }
        //把前端传入的数据,保存到数据库中
        //controller--->调用service----调用mapper
        //创建时间
        //修改时间
        //应为自动填充
        boolean isFinish = categoryService.save(categoryParam);
        return isFinish ? R.success(AntPathmathcherSS.FINISH) : R.error(AntPathmathcherSS.FAILED);
    }

    /**
     * 分页
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>>page(Integer page,Integer pageSize){
        log.info("前后联通");
        //分页对象
     Page<Category> pageCategory=new Page<>();
     //当前页要显示多少行
     pageCategory.setSize(pageSize);
     //当前页
     pageCategory.setCurrent(page);
     ////排序字段
        //使用mp的话,条件都是wrapper中的
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        //根据sort字段升序
        wrapper.orderByAsc(Category::getSort);
        //调用service中的分页方法,来查询分页数据
        Page<Category> result = categoryService.page(pageCategory, wrapper);
        return R.success(result);
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String>delete(Long id){
        log.info("前后联通id--->{}",id);
        //mp提供的
      // categoryService.removeById(id);
        //我们调用自己定义的删除方法
        categoryService.deleteByid(id);
      return   R.success(AntPathmathcherSS.FINISH);
    }
    //
    @PutMapping
    public R<String>update(@RequestBody Category categoryParam){
        //分类名称
        if (StringUtils.isBlank(categoryParam.getName())) {
            return    R.error(AntPathmathcherSS.FAILED);
        }
        //排序效验
        if (categoryParam.getType() == null) {
            return  R.error(AntPathmathcherSS.FAILED);
        }
        //分类类型效验
        if (categoryParam.getSort() == null) {
            return   R.error(AntPathmathcherSS.FAILED);
        }
        categoryService.updateById(categoryParam);
        return R.success(AntPathmathcherSS.FINISH);
    }
    /**
     * 修改分类
     */
    @GetMapping("/list")
    public  R<List<Category>> selectDish(Category categoryParam){
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (categoryParam.getType()!=null) {
            wrapper.eq(Category::getType, categoryParam.getType());
            wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        }
        List<Category> list = categoryService.list(wrapper);
        return R.success(list);
    }
}
