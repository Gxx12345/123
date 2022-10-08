package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输类
 *
 * @author t3rik
 * @since 2022/9/27 12:14
 */
@Data
public class DishDto extends Dish {
    /**
     * 菜品口味
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
