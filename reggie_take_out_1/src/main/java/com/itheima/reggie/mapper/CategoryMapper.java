package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类持久层
 *
 * @author t3rik
 * @since 2022/9/26 11:47
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
