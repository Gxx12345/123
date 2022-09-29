package com.itheima.ruji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.ruji.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * C端用户信息持久层
 *
 * @autho Gzz
 * @since 2022/9/29 18:15
 */
@Mapper
public interface IUserMapper extends BaseMapper<User> {
}
