package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.ICategoryService;
import com.itheima.reggie.service.IDishFlavorService;
import com.itheima.reggie.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜品控制器
 *
 * @author t3rik
 * @since 2022/9/27 11:38
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private IDishService dishService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IDishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @PostMapping
    public R<String> sava(@RequestBody DishDto dishDto) {
        log.info("前后端联通");
        // 如果引入了缓存
        // 那么在对数据库中的数据进行修改或者是新增或者删除的时候,都需要清理下我们的缓存
        // 清理的话,要用key
        // 现在是以分类作为key保存的数据,那么这里就要拿到分类的id,拼接一个一样的key,来做删除
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        // 清理掉这个数据
        this.redisTemplate.delete(key);
        this.dishService.saveWithFlavor(dishDto);
        return R.success(GlobalConstant.FINISH);
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        log.info("前后端联通");
        // 1. 构造分页条件对象
        Page<Dish> queryPage = new Page<>();
        // 当前页
        queryPage.setCurrent(page);
        // 当前页要显示多少行
        queryPage.setSize(pageSize);
        // 2. 构建查询及排序条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 模糊匹配
        // 判断name是否为空,如果不为空的话,那么就会拼接查询条件
//        if(StringUtils.isNotBlank(name)){
//            queryWrapper.like(Dish::getName,name);
//        }
        queryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);
        // 方便用户使用,更新时间倒序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 3. 执行分页条件查询
        Page<Dish> dishPage = this.dishService.page(queryPage, queryWrapper);
        // 4. 构建返回结果对象，并copy查询结果到该对象中
        Page<DishDto> result = new Page<>();
        // 忽略掉records这个属性,不做赋值的操作
        BeanUtils.copyProperties(dishPage, result, "records");
        // 把Dish对象转为DishDto对象，同时赋值分类名称
        // 使用lambda表达式map的写法
        List<DishDto> dishDtoList = dishPage.getRecords().stream().map(item -> {
            DishDto dishDto = new DishDto();
            // Dish -> DishDto
            BeanUtils.copyProperties(item, dishDto);
            // 分类名称
            Category category = this.categoryService.getById(item.getCategoryId());
            if (category != null) {
                // 分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());

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

        // 6. 封装数据并返回
        result.setRecords(dishDtoList);
        return R.success(result);
    }


    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        // 参数校验
        DishDto dishDto = this.dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("前后端联通");// controller -> service -> mapper
        // 更新这里,需要删除掉菜品相关的所有的缓存
        // 是因为在更新菜品时,可以修改这个菜品的分类
        // 假设数据在更新之前 dishDto.getCategoryId()  = "123"
        // 数据在更新之后,选择了一个其他的分类 dishDto.getCategoryId()  = "321"

//        String keys = "dish_*";
        // 匹配到所有的dish作为开头的key
        Set<Object> keys = redisTemplate.keys("dish_*");
        // 清理掉这个数据
        this.redisTemplate.delete(keys);
        this.dishService.updateWithFlavor(dishDto);
        return R.success(GlobalConstant.FINISH);
    }

    //region 修改前的list方法
    //    /**
//     * 根据分类id查询相应的菜品
//     *
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> getList(Dish dish) {
//        log.info("前后端联通");
//        // 分类ID 查询菜品表
////        Long categoryId = dish.getCategoryId();
//        // 拼接条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        // =
//        // 0.223232323321321654
//        // 还要被js处理 还有其他的语言处理 new BigDecimal();
//        // 0.01
//        // 1
//        if (dish.getCategoryId() != null) {
//            queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
//            // 等同于if判断
////            queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        }
//        // 只查询起售状态的菜品
//        // status
//        // 1 起售
//        // 0 禁售
//        queryWrapper.eq(Dish::getStatus, 1);
//        List<Dish> dishList = this.dishService.list(queryWrapper);
//        return R.success(dishList);
//    }
    //endregion

    /**
     * 根据分类获取菜品数据
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        // 返回的结果
        List<DishDto> dishDtoList;
        // 保存到redis中的key
        // 获取时也要用
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        // 根据这个key,到redis中获取相应的数据
        dishDtoList = (List<DishDto>) this.redisTemplate.opsForValue().get(key);
        if (CollectionUtils.isNotEmpty(dishDtoList)) {
            // 如果要是不为空,就代表这个数据已经在redis缓存中了.
            return R.success(dishDtoList);
        }
        // 如果在redis中没有查询到,会返回一个null
        // 这样的话dishDtoList就是null,所以需要实例化
        dishDtoList = new ArrayList<>();
        // 如果dishDtoList是空,就代表在redis缓存中没有查询到数据,那么就要到数据库中查询.
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        // 根据条件查询数据库
        List<Dish> list = dishService.list(queryWrapper);

        //region foreach写法

        for (Dish item : list) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            // 如果没有实例化的话,会报空指针
            dishDtoList.add(dishDto);
        }
        this.redisTemplate.opsForValue().set(key, dishDtoList);
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
}
