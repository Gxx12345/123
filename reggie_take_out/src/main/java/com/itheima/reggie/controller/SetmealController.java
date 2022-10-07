package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.runtime.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 套餐控制器
 *
 * @author Gmy
 * @since 2022/9/29 11:27
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true) //清除setmealCache名称下,所有的缓存数据
    @ApiOperation(value = "新增套餐接口")
    public R<String> save(@RequestBody SetmealDto dto) {
        log.info("前后端联通");
        //  新增套餐
        //  在service中拓展的方法
        setmealService.saveWithDish(dto);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 套餐查询
     */
    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "name", value = "套餐名称", required = false)
    })
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        //  1. 构建分页条件对象
        Page<Setmeal> queryPage = new Page<>();
        //  当前页
        queryPage.setCurrent(page);
        //  当前页显示多少行
        queryPage.setSize(pageSize);
        //  2. 构建查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);
        //  3. 执行分页查询条件
        Page<Setmeal> setmealPage = setmealService.page(queryPage, queryWrapper);
        //  4. 构建返回结果对象
        Page<SetmealDto> result = new Page<>();
        //  setmealPage -> result
        //  并copy查询结果到该对象中
        BeanUtils.copyProperties(setmealPage, result);
        //  5. 遍历分页查询列表数据
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        for (Setmeal item : setmealPage.getRecords()) {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item, dto);
            //  查询分类
            Category category = this.categoryService.getById(item.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            setmealDtoList.add(dto);
        }
        //  把Setmeal对象转为SetmealDto对象，
        //  同时赋值分类名称
        //  6. 封装数据并返回
        result.setRecords(setmealDtoList);
        return R.success(result);
    }

    /**
     * 删除套餐
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true) //清除setmealCache名称下,所有的缓存数据
    @ApiOperation(value = "套餐删除接口")
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("前后端联通");
        setmealService.deleteByIds(ids);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 套餐状态的修改
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("前后端联通");
        for (Long id : ids) {
            Setmeal byId = setmealService.getById(id);
            byId.setStatus(status);
            setmealService.updateById(byId);
        }
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 根据id获取套餐信息
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "套餐条件查询接口")
    @ApiImplicitParam(name = "id", value = "套餐id", required = true)
    public R<SetmealDto> update(@PathVariable Long id) {
        log.info("前后端联通");
        Optional.ofNullable(id)
                .orElseThrow(() -> new CustomException(GlobalConstant.ERROR_PARAM));
        return R.success(this.setmealService.getSetmealById(id));
    }

    /**
     * 更新套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    @ApiOperation(value = "更新接口")
    @ApiImplicitParam(name = "setmealDto", value = "套餐信息", required = true)
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        Optional.ofNullable(setmealDto.getId())
                .orElseThrow(() -> new CustomException(GlobalConstant.ERROR_PARAM));
        Boolean finished = this.setmealService.updateWithDish(setmealDto);
        return finished ? R.success(GlobalConstant.FINISH) : R.error(GlobalConstant.FAILED);
    }

    /**
     * 根据条件查询套餐
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = this.setmealService.list(queryWrapper);
        return R.success(list);
    }
}
