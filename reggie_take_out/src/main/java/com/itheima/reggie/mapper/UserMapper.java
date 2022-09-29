package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户持久层
 *
 * @author yjiiie6
 * @since 2022/9/29 19:42
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
