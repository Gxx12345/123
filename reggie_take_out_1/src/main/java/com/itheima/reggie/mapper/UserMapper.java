package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author t3rik
 * @since 2022/9/29 17:34
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
