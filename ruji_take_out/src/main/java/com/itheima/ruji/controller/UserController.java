package com.itheima.ruji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.BaseTreadlock;
import com.itheima.ruji.common.CustomException;
import com.itheima.ruji.common.R;
import com.itheima.ruji.entity.User;
import com.itheima.ruji.service.IUserService;
import com.itheima.ruji.utils.SMSUtils;
import com.itheima.ruji.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * C端用户信息控制层
 *
 * @author Gzz
 * @since 2022/9/29 18:20
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
     private IUserService iUserService;

    /**
     * 获取验证码
     * @param user
     * @param httpSession
     * @return
     */
    @PostMapping("/sendMsg")
 public R<String> userR(@RequestBody User user, HttpSession httpSession){
        if (StringUtils.isBlank(user.getPhone())) {
            throw new CustomException("传入的参数不正确");
        }
        //获取用户的手机号
        String phone = user.getPhone();
        //生成随机数
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("生成的验证码:{}",code);
        SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
        //写到session中
        httpSession.setAttribute(phone,code);
       return   R.success(AntPathmathcherSS.FINISH);
    }

    /**
     * 验证C端验证码执行登录
     * @param map
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession httpSession) {
        log.info("前后端联通");
        // 1). 获取前端传递的手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        // 2). 从Session中获取手机号对应的正确的验证码
        String currentCode = (String) httpSession.getAttribute(phone);
        // 为了调试程序
        if ("15661690662".equals(phone) && "0".equals(code)) {
            currentCode = "0";
        }
        // 3). 进行验证码的比对，如果不一致，直接返回登录失败
        // 如果验证码不一致的话,直接返回登录失败
        if (!code.equals(currentCode)) {
            return R.error("登录失败");
        }
        // 如果验证码一致的话,代表登录成功
        // 4). 如果比对成功，需要根据手机号查询当前用户，
        // 查询用户表
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 根据手机号码查询
        queryWrapper.eq(User::getPhone, phone);
        User user = this.iUserService.getOne(queryWrapper);
        // 如果用不存在，则新建一个该手机号的用户。即注册一个新用户
        if (user == null) {
            // 注册新用户
            user = new User();
            user.setPhone(phone);
            // 员工状态启用
            user.setStatus(1);
            user.setName("郭智飞");
            this.iUserService.save(user);
        }
        // 5). 将登录用户的ID存储在Session中
        httpSession.setAttribute(AntPathmathcherSS.C_LOGIN, user.getId());
        return R.success(user);
    }
}
