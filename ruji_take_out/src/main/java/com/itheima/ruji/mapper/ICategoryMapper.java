package com.itheima.ruji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.ruji.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类持久层
 *
 * @autho Gzz
 * @since 2022/9/26 12:04
 */
@Mapper
public interface ICategoryMapper extends BaseMapper<Category> {
}
