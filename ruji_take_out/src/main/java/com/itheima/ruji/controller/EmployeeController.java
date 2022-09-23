package com.itheima.ruji.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.itheima.ruji.common.R;
import com.itheima.ruji.entity.Employee;
import com.itheima.ruji.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private IEmployeeService iEmployeeService;

    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employeeParam, HttpServletRequest request) {
        //前后端调练
        log.info("employee---->", employeeParam.toString());
        //用户名
        /**
         * 登录条件
         */
        String username = employeeParam.getUsername();
        //1.根据页面提交的用户名username 查询数据库
        //拼接查询条件
        LambdaQueryWrapper<Employee> queryChainWrapper = new LambdaQueryWrapper<>();
        //查询用户名在这个员工表是否存在
        //select * from Employee where username="username"
        //下面这个是利用lamada中的方法引用  c从Employee中取出username名字,与username做比较
        queryChainWrapper.eq(Employee::getUsername, username);
        //根据这个查询条件,查询一条记录
        Employee employee = iEmployeeService.getOne(queryChainWrapper);
        //如果没有查询到则返回登录失败结构
        //如果是空,就代表这个用户不存在
        if (employee == null) {
            return R.error("登录失败");
        }
        //将页面提交的密码进行md5 加密
        String password = employeeParam.getPassword();
        //这个是可以进行md5加密的工具类
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        //密码比对,如果不一致登录失败结果
        //与数据库密码进行比对
        if (!(md5Password.equals(employee.getPassword()))){
            return R.error("登录失败");
        }
        //5 查看员工状态,如果为禁用状态,则返回禁用结果
        //员工status 0 :禁用 1:启用
        //如果员工已被禁用
        if (employee.getStatus() == 0) {
            return R.error("登录失败");
        }
        //6 登录成功,将员工id 存入Session并返回登录成果
        //这个是要把登录信息存在Sseeion中,  存入的是键值对类型 -->"employee" 是键.  employee.getId()是值
        request.getSession().setAttribute("employee", employee.getId());
        return R.success(employee);
    }
    /**
     * 登出接口
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("登录成功");
    }
}

