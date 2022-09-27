package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类持久层
 *
 * @author L
 * @since 2022/9/26 12:03
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
