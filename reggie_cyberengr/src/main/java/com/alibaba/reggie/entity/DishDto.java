package com.alibaba.reggie.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品管理
 *
 * @author cyberengr
 * @since 2022/9/26 20:34
 */
@Data
public class DishDto extends Dish{
    private List<DishFlavor> flavors = new ArrayList<>();
    private String categoryName; //菜品分类名称
    private Integer copies;  // 份数
}
