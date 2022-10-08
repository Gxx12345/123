package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.IUserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户控制器
 *
 * @author t3rik
 * @since 2022/9/29 17:35
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;
    /**
     * 操作redis的对象
     */
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession) {
        log.info("前后端联通");
        // 1.获取用户的手机号
        String phone = user.getPhone();
        // 2.生成随机数
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        // 3.调用短信工具类发送短信
//        SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", phone, code);
        log.info("本次发送的验证码====> {}", code);
        // 4.写入到session中
//        httpSession.setAttribute(phone, code);h
        // 第一个参数 是key
        // 第二个参数是 value
        // 第三个参数是时间
        // 第四个参数是时间单位
        // 把phone作为key,code作为value写入到redis中,过期时间是5分钟
        redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
        return R.success(GlobalConstant.FINISH);
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession httpSession) {
        log.info("前后端联通");
        // 1). 获取前端传递的手机号和验证码
        String phone = map.get("phone").toString();
        // 登录时使用的验证码
        String code = map.get("code").toString();
        //region 从Session中
        // 2). 从Session中获取手机号对应的正确的验证码
//        String currentCode = (String) httpSession.getAttribute(phone);
        //endregion
        // 2). redis中获取手机号对应的正确的验证码
        String currentCode = (String) this.redisTemplate.opsForValue().get(phone);
        // currentCode = 0
        // currentCode = "0";
        // 相当于加了个后门
        // 实际开发的时候不要这么写
        // 刑法中有写
        if ("18510408337".equals(phone) && "0".equals(code)) {
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
        User user = this.userService.getOne(queryWrapper);
        // 如果用不存在，则新建一个该手机号的用户。即注册一个新用户
        if (user == null) {
            // 注册新用户
            user = new User();
            user.setPhone(phone);
            // 员工状态启用
            user.setStatus(1);
            this.userService.save(user);
        }
        // 5). 将登录用户的ID存储在Session中
        httpSession.setAttribute(GlobalConstant.MOBILE_KEY, user.getId());
        // 登录成功后删除掉验证码
        // 就能做到,这个验证码只有一次有效
        // 如果不删除,用一样的手机号码及一样的验证码,可以做到一直能登录成功
        this.redisTemplate.delete(phone);
        return R.success(user);
    }
}
