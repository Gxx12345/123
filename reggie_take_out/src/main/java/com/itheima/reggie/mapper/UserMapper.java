package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Gmy
 * @since 2022/9/29 18:10
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
