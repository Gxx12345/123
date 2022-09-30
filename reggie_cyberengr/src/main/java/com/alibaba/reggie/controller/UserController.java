package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.entity.User;
import com.alibaba.reggie.service.IUserService;
import com.alibaba.reggie.util.SMSUtils;
import com.alibaba.reggie.util.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * UserController
 *
 * @author cyberengr
 * @since 2022/9/29 18:09
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession httpSession) {
        if (StringUtils.isEmpty(user.getPhone())) {
            return Result.error("请输入手机号");
        }
        String code = String.valueOf(ValidateCodeUtils.generateValidateCode(4));
        //SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", user.getPhone(), code);
        httpSession.setAttribute(GlobalConstant.USER_KEY, code);
        return Result.success(GlobalConstant.FINISHED);
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session) {
        String phone = (String) map.get("phone");
        /*String code = (String) map.get("code");

        String attribute = (String) session.getAttribute(GlobalConstant.USER_KEY);
        if (StringUtils.isBlank(attribute) || !code.equals(attribute)) {
            return Result.error(GlobalConstant.FAILED);
        }*/
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(phone),User::getPhone,phone);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        if (0 == user.getStatus()) {
            return Result.error("账号已禁用");
        }
        //设置session值
        session.setAttribute(GlobalConstant.USER_KEY, user.getId());
        return Result.success(user);
    }

    @PostMapping("/loginout")
    public Result<String> loginout(HttpSession session) {
        session.removeAttribute(GlobalConstant.USER_KEY);
        return Result.success("退出登录!");
    }
}
