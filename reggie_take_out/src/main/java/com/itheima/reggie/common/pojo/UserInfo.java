package com.itheima.reggie.common.pojo;

import lombok.Data;

/**
 * 登录用户信息
 *
 * @author gmy
 * @since 2022/9/28 16:19
 */
@Data
public class UserInfo {
    /**
     * 当前登录用户id
     */
    private Long id;
    /**
     * 当前登录用户名
     */
    private String username;
}
