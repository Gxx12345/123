package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息
 *
 * @author my
 * @since 2022/9/29 20:21
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
