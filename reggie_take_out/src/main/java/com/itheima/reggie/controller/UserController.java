package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.IUserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户信息
 *
 * @author my
 * @since 2022/9/29 19:03
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取用户手机号
        String phone = user.getPhone();
        //生产随机验证码
        //String code = ValidateCodeUtils.generateValidateCode(4).toString();
        //调用短信工具类发送短信
        //SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
        //写入到session中
        String code = "1234";
        session.setAttribute(phone,code);
        return R.success(GlobalConstant.FINISH);
    }

    @PostMapping("/login")
    @Transactional
    public R<User> login(@RequestBody Map map,HttpSession httpSession){
        //获取前端传入的号码和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从session中获得正确的验证码
        String currentCode = (String) httpSession.getAttribute(phone);
        //验证码比对
        if(!code.equals(currentCode)){
            throw new CustomException("验证码错误");
        }
        //如果验证码正确，根据手机号搜索用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user = this.userService.getOne(queryWrapper);
        if(user == null){
            //如果用户不存在，就注册一个用户
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            this.userService.save(user);
        }
        httpSession.setAttribute(GlobalConstant.MOBILE_KEY,user.getId());
        return R.success(user);
    }
}
