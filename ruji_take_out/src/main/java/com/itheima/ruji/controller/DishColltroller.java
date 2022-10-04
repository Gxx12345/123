package com.itheima.ruji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.CustomException;
import com.itheima.ruji.common.R;
import com.itheima.ruji.dto.DishDto;
import com.itheima.ruji.entity.Category;
import com.itheima.ruji.entity.Dish;
import com.itheima.ruji.entity.DishFlavor;
import com.itheima.ruji.service.ICategoryService;
import com.itheima.ruji.service.IDishFlavorService;
import com.itheima.ruji.service.IDishService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.runtime.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Delete;
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
    @Autowired
    private IDishFlavorService iDishFlavorService;
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

    /**
     * 根据id查询回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> catgeoryId(@PathVariable Long id){
        if(id<=0){
            R.error(AntPathmathcherSS.ERROR);
        }
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }

    /**
     * 修改
     * @param dishDto
     * @return
     */
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
   /* @GetMapping("/list")
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
*/

    /**
     * C端查询列表
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        //region foreach写法
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish item : list) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = iCategoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = iDishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            dishDtoList.add(dishDto);
        }
        //endregion
        //region Lambda使用Map方法的写法
        //        List<DishDto> dishDtoList = list.stream().map((item) -> {
//            DishDto dishDto = new DishDto();
//            BeanUtils.copyProperties(item,dishDto);
//            Long categoryId = item.getCategoryId();//分类id
//            //根据id查询分类对象
//            Category category = categoryService.getById(categoryId);
//            if(category != null){
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
//            //当前菜品的id
//            Long dishId = item.getId();
//            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
//            //SQL:select * from dish_flavor where dish_id = ?
//            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
//            dishDto.setFlavors(dishFlavorList);
//            return dishDto;
//        }).collect(Collectors.toList());
        //endregion
        return R.success(dishDtoList);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除菜品/批量删除菜品")
    @ApiImplicitParam(name = "ids", value = "菜品id集合", required = true)
    public R<String>deletes(@RequestParam List<Long>ids){
        if (CollectionUtils.isEmpty(ids)) {
            throw new CustomException(AntPathmathcherSS.ERROR);
        }
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Dish::getId,ids);
        dishService.remove(wrapper);
        return R.success(AntPathmathcherSS.FINISH);
    }

    /**
     * 停售
     * @param ids
     * @return
     */
//    @PostMapping("/status/0")
//
//    public R<String>updateStatus( Long ids){
//        log.info("前后联通");
//        if (ids==null){
//            throw new CustomException("传入的数据有误");
//        }
//        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Dish::getId,ids);
//        List<Dish> list = dishService.list(wrapper);
//        for (Dish dishlist : list) {
//            dishlist.setStatus(0);
//        }
//        dishService.updateBatchById(list);
//        return R.success(AntPathmathcherSS.FINISH);
//    }
//

    /**
     * 批量起售
     * @return
     */
    @PostMapping("status/{statusValue}")
    @ApiOperation(value = "停售启售/批量启售/停售")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "菜品id集合", required = true),
            @ApiImplicitParam(name = "statusValue", value = "状态", required = true)}
    )
    public R<String>bulkStartingDish(@RequestParam List<Long>ids,@PathVariable Integer statusValue){
        log.info("前后联通");
        if (CollectionUtils.isEmpty(ids)) {
            throw new CustomException(AntPathmathcherSS.ERROR);
        }
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Dish::getId,ids);
        List<Dish> list = dishService.list(wrapper);
        for (Dish dishlist : list) {
            dishlist.setStatus(statusValue);
        }
        dishService.updateBatchById(list);
        return R.success(AntPathmathcherSS.FINISH);
    }

    /**
     * 批量停售
     * @param ids
     * @return
     */
   /* @PostMapping("/status/{statusValue}")
    public R<String>stopBatchesDish(@RequestParam List<Long>ids,@PathVariable Integer statusValue){
        log.info("前后联通");
        if (CollectionUtils.isEmpty(ids)) {
            throw new CustomException(AntPathmathcherSS.ERROR);
        }
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Dish::getId,ids);
        List<Dish> list = dishService.list(wrapper);
        for (Dish dishlist : list) {
            dishlist.setStatus(statusValue);
        }
        dishService.updateBatchById(list);
        return R.success(AntPathmathcherSS.FINISH);
    }*/
}
