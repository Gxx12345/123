package com.alibaba.reggie.mapper;

import com.alibaba.reggie.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper
 *
 * @author cyberengr
 * @since 2022/9/29 18:06
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
