package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类dao数据访问层
 *
 * @author yjiiie6
 * @since 2022/9/26 12:08
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}