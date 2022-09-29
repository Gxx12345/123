package com.itheima.ruji.dto;

import com.itheima.ruji.entity.Setmeal;
import com.itheima.ruji.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
