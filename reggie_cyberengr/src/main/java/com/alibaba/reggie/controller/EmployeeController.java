package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.entity.Employee;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.service.IEmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private IEmployeeService service;

    @PostMapping("/login")
    public  Result<Employee> login(@RequestBody Employee employeeParam, HttpServletRequest request) {
        //判断用户名和密码是否为空
        if (StringUtils.isBlank(employeeParam.getUsername()) || StringUtils.isBlank(employeeParam.getPassword())) {
            return Result.error("登录失败!");
        }
        //根据username查询用户信息
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employeeParam.getUsername());
        Employee employee = service.getOne(wrapper);
        String password = DigestUtils.md5DigestAsHex(employeeParam.getPassword().getBytes());
        //判断是否有该用户,密码是否正确,是否禁用
        if (null == employee) {
            return Result.error("登录失败!");
        }
        if (!password.equals(employee.getPassword())) {
            return Result.error("登录失败!");
        }
        if (0 == employee.getStatus()) {
            return Result.error("账号已禁用");
        }
        //设置session值
        request.getSession().setAttribute(GlobalConstant.EMPLOYEE_KEY,employee.getId());
        return Result.success(employee);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        //移除session值
        request.getSession().removeAttribute(GlobalConstant.EMPLOYEE_KEY);
        return Result.success("退出成功！");
    }

}
