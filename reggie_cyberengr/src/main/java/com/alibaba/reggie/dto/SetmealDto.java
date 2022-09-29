package com.alibaba.reggie.dto;


import com.alibaba.reggie.entity.Setmeal;
import com.alibaba.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
