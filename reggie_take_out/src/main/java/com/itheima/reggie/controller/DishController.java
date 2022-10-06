package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CustomException;
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
import java.util.concurrent.TimeUnit;

/**
 * 菜品控制层
 *
 * @author yjiiie6
 * @since 2022/9/27 14:09
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private IDishService iDishService;
    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private IDishFlavorService iDishFlavorService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDtoParam
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDtoParam) {

        iDishService.saveWithFlavor(dishDtoParam);

        //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDtoParam.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success(GlobalConstant.FINISH);
    }


    /**
     * 菜品分页查询
     *
     * @param page     当前页
     * @param pageSize 当前页显示记录数
     * @param name     查询条件
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        // 构造分页条件对象  并设置当前页及当前页显示记录数
        Page<Dish> queryPage = new Page<>(page, pageSize);

        // 构建查询及排序条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 模糊匹配  -- 校验name
        queryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);
        // 方便用户使用，更新时间倒序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页条件查询
        Page<Dish> dishPage = iDishService.page(queryPage, queryWrapper);

        // 构建返回结果对象 ， 并copy查询结构到该对象中
        Page<DishDto> result = new Page<>();
        // 忽略掉records这个属性，不做赋值的操作
        BeanUtils.copyProperties(dishPage, result, "records");

        //region lambda表达式map写法
        // 使用lambda表达式map的写法
        /*List<DishDto> dishDtoList = dishPage.getRecords().stream().map(item -> {
            DishDto dishDto = new DishDto();
            // Dish -> DishDto
            BeanUtils.copyProperties(item, dishDto);
            // 分类名称
            Category category = iCategoryService.getById(item.getCategoryId());
            if (category != null) {
                // 分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;

        }).collect(Collectors.toList());*/
        //endregion

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

        //region for循环
        // 遍历分页查询列表数据
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish item : dishPage.getRecords()) {
            DishDto dishDto = new DishDto();
            // Dish —> DishDto
            BeanUtils.copyProperties(item, dishDto);
            // 分类名称
            Category category = iCategoryService.getById(item.getCategoryId());
            if (category != null) {
                // 分类名称赋值
                dishDto.setCategoryName(category.getName());
            }
            // 把数据添加到集合中
            dishDtoList.add(dishDto);
        }
        //endregion

        // 把Dish对象转为DishDto对象，同时赋值分类名称
        // 封装数据并返回
        result.setRecords(dishDtoList);
        return R.success(result);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     *
     * @param id
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = iDishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 更新菜品信息，同时更新对应的口味信息
     *
     * @param dishDtoParam
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDtoParam) {

        iDishService.updateWithFlavor(dishDtoParam);

        //清理所有菜品的缓存数据
        Set<Object> keys = redisTemplate.keys("dish_*"); //获取所有以dish_xxx开头的key
        redisTemplate.delete(keys); //删除这些key

        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish
     * @return
     */
    //@GetMapping("/list")
    //public R<List<Dish>> getList(Dish dish) {
    //    Long categoryId = dish.getCategoryId();
    //
    //    //构造查询条件
    //    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    //    queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
    //    //添加条件，查询状态为1（起售状态）的菜品
    //    queryWrapper.eq(Dish::getStatus, 1);
    //    List<Dish> dishList = iDishService.list(queryWrapper);
    //
    //    return R.success(dishList);
    //}

    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        // 返回的结果
        List<DishDto> dishDtoList;

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = iDishService.list(queryWrapper);

        //动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);
        if (CollectionUtils.isNotEmpty(dishDtoList)) {
            //如果存在，直接返回，无需查询数据库
            return R.success(dishDtoList);
        }

        dishDtoList = new ArrayList<>(); // 不实例化则会造成空指针

        //region for循环写法
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
            // 不实例化则会造成空指针
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

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
        redisTemplate.opsForValue().set(key,dishDtoList);
        return R.success(dishDtoList);
    }


    /**
     * 批量删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(@RequestParam List<Long> ids) {
        if (ids == null) {
            return R.error("传入的参数有误");
        }

        ids.forEach(id -> {
            Dish dish = iDishService.getById(id);
            if (dish.getStatus() == 1) {
                throw new CustomException("该菜品正在出售，无法删除");
            }
            iDishService.removeByIds(ids);
        });

        return R.success(GlobalConstant.FINISH);
    }


    /**
     * 批量停售起售
     *
     * @param status 路径参数 0 停售 1 起售
     * @param ids id集合
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("前后端联通");

        // 参数校验
        if (ids == null) {
            throw new CustomException("传入的参数有误");
        }
        // 遍历id集合获取到每一个id
        for (Long id : ids) {
            // 根据id获取到相应的菜品对象
            Dish dish = iDishService.getById(id);
            // 修改状态
            dish.setStatus(status);
            // 调用mp方法修改对象
            iDishService.updateById(dish);
        }

        return R.success(GlobalConstant.FINISH);
    }

}
