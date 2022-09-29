package com.itheima.ruji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.R;
import com.itheima.ruji.dto.DishDto;
import com.itheima.ruji.entity.Category;
import com.itheima.ruji.entity.Dish;
import com.itheima.ruji.service.ICategoryService;
import com.itheima.ruji.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品表控制层
 *
 * @author Gzz
 * @since 2022/9/27 14:55
 */


@RestController
@Slf4j
@RequestMapping("/dish")
public class DishColltroller {
    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @Autowired
    private IDishService dishService;
    @Autowired
    private ICategoryService iCategoryService;
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("前后端联通");
        // 调用dishService的业务方法，完成保存菜品
        dishService.saveWithFlavor(dishDto);
        return R.success(AntPathmathcherSS.FINISH);
    }

    /**
     * 菜品分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>>page(Integer page,Integer pageSize,String name){
        // 1. 构造分页条件对象
        Page<Dish> objectPage = new Page<>();
        // 当前页要显示多少行
        objectPage.setSize(pageSize);
        // 当前页
        objectPage.setCurrent(page);
        // 2. 构建查询及排序条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        // 模糊匹配
        // 判断name是否为空,如果不为空的话,那么就会拼接查询条件
//        if(StringUtils.isNotBlank(name)){
//            queryWrapper.like(Dish::getName,name);
//        }
        wrapper.like(StringUtils.isNotBlank(name) ,Dish::getName,name);
        // 方便用户使用,更新时间倒序
        wrapper.orderByDesc(Dish::getUpdateTime);
        // 3. 执行分页条件查询
        Page<Dish> dishPage = dishService.page(objectPage, wrapper);
        // 4. 构建返回结果对象，并copy查询结果到该对象中
        Page<DishDto> dtoPage = new Page<>();
        // 忽略掉records这个属性,不做赋值的操作
        BeanUtils.copyProperties(dishPage,dtoPage,"records");
        List<DishDto>dishDtoList=new ArrayList<>();
        // 把Dish对象转为DishDto对象，同时赋值分类名称
        // 使用lambda表达式map的写法

        for (Dish item : dishPage.getRecords()) {
            DishDto dishDto=new DishDto();
            // Dish -> DishDto
            BeanUtils.copyProperties(item,dishDto);
            // 分类名称
            Category category =iCategoryService.getById(item.getCategoryId());
            if ((category!=null)){
                // 分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            dishDtoList.add(dishDto);
        }
        //region lambbda表达式
        //region 使用lambda表达式的foreach实现
        //        List<DishDto> dishDtoList = new ArrayList<>();
//        dishPage.getRecords().forEach(item -> {
//            DishDto dishDto = new DishDto();
//            // Dish -> DishDto
//            BeanUtils.copyProperties(item, dishDto);
//            // 分类名称
//            Category category = this.categoryService.getById(item.getCategoryId());
//            if (category != null) {
//                // 分类名称赋值
//                dishDto.setCategoryName(category.getName());
//                dishDtoList.add(dishDto);
//            }
//        });
        //endregion
        //endregion

        //region for循环
        //region for循环遍历列表
        // 5. 遍历分页查询列表数据
//        List<DishDto> dishDtoList = new ArrayList<>();
//        for (Dish item : dishPage.getRecords()) {
//            DishDto dishDto = new DishDto();
//            // Dish -> DishDto
//            BeanUtils.copyProperties(item, dishDto);
//            // 分类名称
//            Category category = this.categoryService.getById(item.getCategoryId());
//            if (category != null) {
//                // 分类名称赋值
//                dishDto.setCategoryName(category.getName());
//            }
//            // 把数据添加到集合中
//            dishDtoList.add(dishDto);
//        }
        //endregion
        //endregion

        // 6. 封装数据并返回
        dtoPage.setRecords(dishDtoList);
        return R.success(dtoPage);
    }
    @GetMapping("/{id}")
    public R<DishDto> catgeoryId(@PathVariable Long id){
        if(id<=0){
            R.error(AntPathmathcherSS.ERROR);
        }
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("前后端联通");// controller -> service -> mapper
        this.dishService.updateWithFlavor(dishDto);
        return R.success(AntPathmathcherSS.FINISH);
    }
    /**
     * 根据分类id查询相应的菜品
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>>listDish(Dish dish){
        //拼接条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //钱数0.222222222225644898
        //还要最终被js处理. 设计到这个钱数的问题,我们一般拿分来换算.先给他以分的方式计算.
        //最终js内部还要进行转换
        wrapper.eq(Dish::getStatus,1);
        //1 起售
        //2 禁售
        List<Dish> list = dishService.list(wrapper);
        return R.success(list);
    }
}
