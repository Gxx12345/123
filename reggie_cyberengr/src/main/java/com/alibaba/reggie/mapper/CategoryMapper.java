package com.alibaba.reggie.mapper;

import com.alibaba.reggie.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类管理
 *
 * @author cyberengr
 * @since 2022/9/26 12:03
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
