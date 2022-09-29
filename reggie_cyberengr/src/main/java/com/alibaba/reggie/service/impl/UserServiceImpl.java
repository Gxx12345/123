package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.entity.User;
import com.alibaba.reggie.mapper.UserMapper;
import com.alibaba.reggie.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl
 *
 * @author cyberengr
 * @since 2022/9/29 18:08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
}
