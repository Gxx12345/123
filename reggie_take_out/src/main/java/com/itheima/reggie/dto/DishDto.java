package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品扩展类
 *
 * @author yjiiie6
 * @since 2022/9/27 14:04
 */
@Data
public class DishDto extends Dish {

    /**
     * 菜品口味集合
     */
    private List<DishFlavor> flavors = new ArrayList<>();

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 份数
     */
    private Integer copies;

}
