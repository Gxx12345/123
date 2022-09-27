package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.GlobalConstant;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.service.ICategoryService;
import com.itheima.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品控制层
 *
 * @author L
 * @since 2022/9/27 15:03
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private IDishService dishService;

    @Autowired
    private ICategoryService categoryService;
    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("前后端联通");
        // 调用dishService的业务方法，完成保存菜品
        this.dishService.saveWithFlavor(dishDto);
        return R.success(GlobalConstant.FINISH);
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        log.info("前后端联通");
        //1.构造分页条件对象
        Page<Dish> queryPage = new Page<>();
        //当前页
        queryPage.setCurrent(page);
        //当前页要先是多少行
        queryPage.setSize(pageSize);
        //2.构造查询及排序条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //模糊匹配
        //判断name是否为空，如果不为空的话，那么就会拼接查询条件
        /*if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(Dish::getName,name);
        }*/
        queryWrapper.like(StringUtils.isNotBlank(name),Dish::getName,name);
        //方便用户使用，更新时间倒序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //3.执行分页条件查询
        Page<Dish> dishPage = this.dishService.page(queryPage, queryWrapper);
        //4.构建返回结果对象，并copy查询结果到该对象中
        Page<DishDto> result = new Page<>();
        //忽略掉records这个属性，不做赋值的操作
        BeanUtils.copyProperties(dishPage,result,"records");
        //5.遍历分页查询列表数据
        /*List<DishDto> dishDtoList = new ArrayList<>();
        for (DishDto item : dishDtoList) {
            DishDto dishDto = new DishDto();
            //Dish -> DishDto
            BeanUtils.copyProperties(item,dishDto);
            //分类名称
            Category category = this.categoryService.getById(item.getCategoryId());
            if (category != null) {
                //分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            //把数据添加到集合中
            dishDtoList.add(dishDto);
        }*/
        //使用lambda表达式mp写法
        List<DishDto> dishDtoList = dishPage.getRecords().stream().map(item -> {
            DishDto dishDto = new DishDto();
            //Dish -> DishDto
            BeanUtils.copyProperties(item,dishDto);
            //分类名称
            Category category = this.categoryService.getById(item.getCategoryId());
            if (category != null) {
                //分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        result.setRecords(dishDtoList);
        return R.success(result);
    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        log.info("前后端联通");
        DishDto dishDto = this.dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("前后端联通");
        this.dishService.updateWithFlavor(dishDto);
        return R.success(GlobalConstant.FINISH);
    }
}
