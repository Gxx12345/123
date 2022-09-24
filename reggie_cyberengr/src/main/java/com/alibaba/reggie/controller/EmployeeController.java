package com.alibaba.reggie.controller;

import com.alibaba.reggie.entity.Employee;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.service.IEmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employeeParam.getUsername());
        Employee employee = service.getOne(wrapper);
        String password = DigestUtils.md5DigestAsHex(employeeParam.getPassword().getBytes());
        if (null == employee) {
            return Result.error("登录失败!");
        }
        if (!password.equals(employee.getPassword())) {
            return Result.error("登录失败!");
        }
        if (0 == employee.getStatus()) {
            return Result.error("账号已禁用");
        }
        request.getSession().setAttribute("employee",employee.getId());
        return Result.success(employee);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功！");
    }

}
