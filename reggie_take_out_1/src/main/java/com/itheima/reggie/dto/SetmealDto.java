package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal{

    /**
     * 套餐和菜品的关系表
     * 菜品信息
     */
    private List<SetmealDish> setmealDishes;

    /**
     * 首页上有套餐分类
     * 套餐分类的名称
     */
    private String categoryName;


}
