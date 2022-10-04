package com.itheima.ruji.controller;

import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.CustomException;
import com.itheima.ruji.common.R;
import com.itheima.ruji.dto.SetmealDto;
import com.itheima.ruji.entity.Category;
import com.itheima.ruji.entity.Dish;
import com.itheima.ruji.entity.Setmeal;
import com.itheima.ruji.service.ICategoryService;
import com.itheima.ruji.service.ISetmealDishService;
import com.itheima.ruji.service.ISetmealService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.runtime.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 套餐控制层
 *
 * @author Gzz
 * @since 2022/9/29 11:21
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private ISetmealService iSetmealService;
    @Autowired
    private ISetmealDishService iSetmealDishService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 新增菜品
     * @param dto
     * @return
     */
    @PostMapping
    public R<String> insertSetmeal(@RequestBody SetmealDto dto) {
        log.info("前后联通{}", dto);
        //在service中扩展方法
        iSetmealService.saveSetmeal(dto);
        return R.success(AntPathmathcherSS.FINISH);
    }

    /**
     * 套餐分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> pageSetmeal(Integer page, Integer pageSize, String name) {
        //构造分页条件
        Page<Setmeal> Page = new Page<>();
        Page.setSize(pageSize);
        Page.setCurrent(page);
        //构造查询条件
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);
        //执行分页条件查询
        Page<Setmeal> setmealPage = iSetmealService.page(Page, wrapper);
        //构建返回结果对象
        Page<SetmealDto> dtoPage = new Page<>();
        //setmeal--->dtoPage
        //并copy查询结果到该对象
        BeanUtils.copyProperties(Page, dtoPage, "records");
        //region 普通for
        //     List<SetmealDto> setmealDtoList = new ArrayList<>();
//        for (Setmeal record : Page.getRecords()) {
//            SetmealDto setmealDto = new SetmealDto();
//            BeanUtils.copyProperties(record, setmealDto);
//            //查询分类
//            Category byId = iCategoryService.getById(record.getCategoryId());
//            if (byId != null) {
//                setmealDto.setCategoryName(byId.getName());
//            }
//            setmealDtoList.add(setmealDto);
//        }
//        //把setmeal对象转换为setmealDto对象
//        //同时赋值分类名称
//        //封装数据并返回
//        dtoPage.setRecords(setmealDtoList);
//        return R.success(dtoPage);
//    }
        //endregion
        //region for each
    /*    List<SetmealDto> setmealDtoList = new ArrayList<>();
        Page.getRecords().forEach(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            //查询分类
            Category byId = iCategoryService.getById(item.getCategoryId());
            if (byId != null) {
                setmealDto.setCategoryName(byId.getName());
            }
            setmealDtoList.add(setmealDto);
        });
        dtoPage.setRecords(setmealDtoList);
        return R.success(dtoPage);
    }*/
        //endregion
        List<SetmealDto> setmealDtoList = Page.getRecords().stream().map(itmen -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(itmen, setmealDto);
            //查询分类
            Category byId = iCategoryService.getById(itmen.getCategoryId());
            if (byId != null) {
                setmealDto.setCategoryName(byId.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        //把setmeal对象转化为SetmealDto对象
        //同时赋值分类名称
        //封装数据并返回
        dtoPage.setRecords(setmealDtoList);
        return R.success(dtoPage);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "套餐删除接口")
   public R<String>deleteSetmeal(@RequestParam List<Long>ids){
        log.info("前后联通:{}",ids.toString());
        if (CollectionUtils.isEmpty(ids)) {
            throw new CustomException(AntPathmathcherSS.ERROR);
        }
        iSetmealService.deleteSetmeals(ids);
        return R.success(AntPathmathcherSS.FINISH);

}
    /**
     * C端根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list( Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = iSetmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 批量停售
     * @param ids
     * @return
     */
  /*  @PostMapping("/status/{statusValue}")
    @ApiOperation(value = "套餐启售停售接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "id集合", required = true),
            @ApiImplicitParam(name = "statusValue", value = "状态", required = true)
    })
    public R<String>stopBatchesSetmeal(@RequestParam List<Long>ids,@PathVariable Integer statusValue){
        log.info("前后联通");
        if (ids==null){
            throw new CustomException("传入的数据有误");
        }
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = iSetmealService.list(wrapper);
        for (Setmeal dishlist : list) {
            dishlist.setStatus(statusValue);
        }
        iSetmealService.updateBatchById(list);
        return R.success(AntPathmathcherSS.FINISH);
    }*/
    /**
     * 批量起售
     * @param ids
     * @return
     */
    @PostMapping("/status/{statusValue}")
    @ApiOperation(value = "套餐启售停售接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "id集合", required = true),
            @ApiImplicitParam(name = "statusValue", value = "状态", required = true)
    })
    public R<String>bulkStartingSetmeal(@RequestParam List<Long>ids,@PathVariable Integer statusValue){
        log.info("前后联通");
        if (ids==null){
            throw new CustomException("传入的数据有误");
        }
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = iSetmealService.list(wrapper);
        for (Setmeal dishlist : list) {
            dishlist.setStatus(statusValue);
        }
        iSetmealService.updateBatchById(list);
        return R.success(AntPathmathcherSS.FINISH);
    }

    /**
     * 根据id查询回调
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取套餐接口")
    public R<SetmealDto>setmealId(@PathVariable Long id){
        log.info("前后联通{}",id);
        Optional.ofNullable(id)
                .orElseThrow(() -> new CustomException(AntPathmathcherSS.ERROR));
        SetmealDto dto=iSetmealService.getSetmealId(id);
        return R.success(dto);
    }

    /**
     * 修改
     * @param setmealDto
     * @return
     */
    @PutMapping
    @ApiOperation(value = "更新接口")
    @ApiImplicitParam(name = "setmealDto", value = "套餐信息", required = true)
    public R<String>setmealPut(@RequestBody SetmealDto setmealDto){
         log.info("前后联通{}",setmealDto);
        Optional.ofNullable(setmealDto.getId())
                .orElseThrow(() -> new CustomException(AntPathmathcherSS.ERROR));
         iSetmealService.updateDishSetmeal(setmealDto);
        return R.success(AntPathmathcherSS.FINISH);
    }
}
