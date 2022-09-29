package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.entity.User;
import com.itheima.ruji.mapper.IUserMapper;
import com.itheima.ruji.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * C端用户信息业务层实体类
 *
 * @author Gzz
 * @since 2022/9/29 18:17
 */

@Service
public class IUserServiceImpl extends ServiceImpl<IUserMapper, User> implements IUserService {
}
