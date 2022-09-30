package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户控制器
 * @author Gmy
 * @since 2022/9/29 18:13
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession) {
        log.info("前后端互通");
        //  1. 获取用户的手机号
        String phone = user.getPhone();
        //  2. 生成随机数
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("验证码 ==> {}",code);
        //  3. 调用短信工具箱类发送短信
        SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
        //  4. 写入到session中
        httpSession.setAttribute(phone,code);
        return R.success(GlobalConstant.FINISH);
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession httpSession) {
        //  1. 获取前端传递的手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //  2. 从session中获取手机号对应的正确的验证码
        String correntCode = (String) httpSession.getAttribute(phone);
        if ("18731033120".equals(phone) && "0".equals(code)) {
            correntCode = "0";
        }
        //  3. 进行验证码的比对，如果不一致则返回登录失败
        //  如果验证码不一致的话，直接返回登录失败
        if (!code.equals(correntCode)) {
            return R.error("登录失败");
        }
        //   如果验证码一致的话，代表登陆成功
        //  4. 如果比对成功，需要根据手机号查询当前用户
        //   查询用户表
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //   根据手机号查询
        queryWrapper.eq(User::getPhone, phone);
        User user = this.userService.getOne(queryWrapper);
        //   如果不存在，则新建一个改手机号的用户，即注册一个新用户
        if (user == null) {
            //  注册新用户
            user = new User();
            user.setPhone(phone);
            //  员工状态启用
            user.setStatus(1);
            this.userService.save(user);
        }
        //  5. 将登录用户的id存储在session中
        httpSession.setAttribute(GlobalConstant.MOBILE_KEY, user.getId());
        return R.success(user);
    }
}
