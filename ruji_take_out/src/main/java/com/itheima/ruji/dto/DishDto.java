package com.itheima.ruji.dto;

import com.itheima.ruji.entity.Dish;
import com.itheima.ruji.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**  实体类扩展(数据传输类)
 * @author Gzz
 * @since 2022/9/27 14:58
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
