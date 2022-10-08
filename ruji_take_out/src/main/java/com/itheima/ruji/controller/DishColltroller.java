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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.runtime.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 菜品表控制层
 *
 * @author Gzz
 * @since 2022/9/27 14:55
 */


@RestController
@Slf4j
@RequestMapping("/dish")
@Api(tags = "菜品相关接口")
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
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @PostMapping
    @ApiOperation(value = "新增菜品")
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("前后端联通");
        // 如果引入了缓存
        // 那么在对数据库中的数据进行修改或者是新增或者删除的时候,都需要清理下我们的缓存
        // 清理的话,要用key
        // 现在是以分类作为key保存的数据,那么这里就要拿到分类的id,拼接一个一样的key,来做删除
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        // 清理掉这个数据
        this.redisTemplate.delete(key);
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
    @ApiOperation(value = "菜品分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true),
            @ApiImplicitParam(name = "name",value = "套餐名称",required = false)
    })
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
    @ApiOperation(value = "根据id查询回显")
    @ApiImplicitParam(name = "id",value = "传入ID",required = true)
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
    @ApiOperation(value = "修改菜品")
    @ApiImplicitParam(name = "dishDto",value = "修改数据的Dto",required = true)
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
    @ApiOperation(value = "C端查询列表")
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
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        // 根据条件查询数据库
        List<Dish> list = dishService.list(queryWrapper);

        // 如果在redis中没有查询到,会返回一个null
        // 这样的话dishDtoList就是null,所以需要实例化
         dishDtoList = new ArrayList<>();
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
            // 如果没有实例化的话,会报空指针
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
        this.redisTemplate.opsForValue().set(key, dishDtoList);
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
