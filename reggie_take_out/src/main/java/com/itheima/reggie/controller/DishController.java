package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 新增菜品
 *
 * @author Gmy
 * @since 2022/9/27 15:11
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("前后端联通");
        //  如果引入了缓存
        //  那么在对数据库中的数据进行修改或者是新增或者删除的时候，都需要清理下我们的缓存
        //  清理的话，要用key
        //  现在是以分类作为key保存的数据，那么这里就要拿到这个分类的id，拼接一个一样的key，来做删除
       String key =  "dish_" + dishDto.getCategoryId() + "_1";
       //   清除这个数据
        this.redisTemplate.delete(key);
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    public R<Page<DishDto>> getPage(Integer page, Integer pageSize, String name) {
        log.info("前后端联通");
        //  1. 构造分页条件对象
        Page<Dish> queryPage = new Page<>();
        //  当前页
        queryPage.setCurrent(page);
        //  当前页要显示多少行
        queryPage.setSize(pageSize);
        //  2. 构建查询及排序条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //  模糊匹配
        //  判断name是否为空，如果不为空的话，那么就会直接拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);
        //  方便用户使用，更新时间倒序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //  3. 执行分页查询条件
        Page<Dish> dishPage = this.dishService.page(queryPage, queryWrapper);
        if (dishPage.getRecords().size() == 0) {
            return R.success(new Page<DishDto>());
        }
        //  4. 构建返回结果对象，并copy查询结果到该对象中
        Page<DishDto> result = new Page<>();
        //  忽略掉records这个属性，不做赋值操作
        BeanUtils.copyProperties(dishPage, result, "records");
        //region 分页查询
        //  5. 遍历分页查询列表数据
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish item : dishPage.getRecords()) {
            DishDto dishDto = new DishDto();
            //  Dish -> DishDto
            BeanUtils.copyProperties(item, dishDto);
            //  分类名称
            Category category = this.categoryService.getById(item.getCategoryId());
            if (category != null) {
                //  分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            //  把数据添加到集合中
            dishDtoList.add(dishDto);
        }
        //endregion
        //region lambda表达式
        //  使用lambda表达式mp的写法
        List<DishDto> dishDtoList2 = dishPage.getRecords().stream().map(item -> {
            DishDto dishDto = new DishDto();
            //  Dish -> DishDto
            BeanUtils.copyProperties(item, dishDto);
            //  分类名称
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                //  分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        //endregion
        //  把Dish对象转为DishDto对象，同时赋值分类名称
        //  6. 封装数据并返回
        result.setRecords(dishDtoList);
        return R.success(result);
    }

    /**
     * 根据id查询菜品信息和对应的的口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        //  参数校验
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    ;

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("前后端联通");
        //  更新这里，需要删除掉菜品相关的所有的缓存
        //  是因为在更新菜品时，可以修改这个菜品的分类
        //  假设数据在更新之前，dishDto.getCategoryId() = "123"
        //  数据在更新之后，选择了一个其他的分类 dishDto.getCategoryId() = "321"

        //  String keys = "dish_*" ;
        //  匹配到所有的dish作为开头的key
        Set<Object> keys = redisTemplate.keys("dish_*");
        //  清除掉这个数据
        this.redisTemplate.delete(keys);
        dishService.updateWithFlavor(dishDto);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 根据分类id查询相应的菜品
     */
    /*@GetMapping("/list")
    public R<List<Dish>> getList(Dish dish) {
        //  拼接条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //  等同于if判断
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //  只查询起售状态的菜品
        //  status 1 起售 0 禁售
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/

    /**
     * 根据id删除菜品
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("前后端联通");
        if (ids != null) {
            for (Long id : ids) {
                Dish dishId = dishService.getById(id);
                if (dishId.getStatus() == 1) {
                    throw new CustomException("该菜品为起售菜品，不能被删除");
                }
                dishService.removeByIds(ids);
            }
        }
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 菜品的状态修改
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids) {
        log.info("前后端联通");
        for (Long id : ids) {
            Dish byId = dishService.getById(id);
            byId.setStatus(status);
            dishService.updateById(byId);
        }
        return R.success(GlobalConstant.FINISH);
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        //  返回的结果
        List<DishDto> dishDtoList;
        //  保存到redis中的key
        //  获取时也要用
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //  根据这个key，到redis中获取相应的数据
        dishDtoList = (List<DishDto>) this.redisTemplate.opsForValue().get(key);
        if (CollectionUtils.isNotEmpty(dishDtoList)) {
            //  如果要是不为空，就代表这个数据已经在redis缓存中了
            return R.success(dishDtoList);
        }
        //  如果在redis中没有查询到，就会返回一个null
        //  这样的话doshDtoList就是null，所以需要实例化
        dishDtoList = new ArrayList<>();
        //  如果dishDtoList是空，就代表在redis缓存中没有查询到数据，那么就要到数据库中查询
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        //region foreach写法
        //List<DishDto> dishDtoList = new ArrayList<>();
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
            dishDtoList.add(dishDto);
        }
        this.redisTemplate.opsForValue().set(key,dishDtoList);
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
