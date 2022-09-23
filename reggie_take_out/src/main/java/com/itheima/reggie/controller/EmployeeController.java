package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("employee")
public class EmployeeController {

    @Autowired
    private IEmployeeService IEmployeeService;

    /**
     * 登录的接口
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employeeParam) {
        //前后端联通
        log.info("employee ==> {}",employeeParam.toString());
        //用户名
        String username = employeeParam.getUsername();
        //1、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 查询用户名在这个员工表中是否存在
        // select * from Employee where username = 'username'
        queryWrapper.eq(Employee::getUsername, username);
        // 根据这个查询条件,查询一条记录
        Employee employee = this.IEmployeeService.getOne(queryWrapper);
        // 2、如果没有查询到则返回登录失败结果
        // 如果是空,就代表这个用户在数据库中不存在
        if (employee == null) {
            return R.error("登录失败");
        }
        // 3、将页面提交的密码password进行md5加密处理
        String password = employeeParam.getPassword();
        // 这个是可以进行md5加密的工具类,spring
        String md5pashword = DigestUtils.md5DigestAsHex(password.getBytes());

        // 4、密码比对，如果不一致则返回登录失败结果
        // 与数据库中的密码进行比对
        if (!(md5pashword.equals(employee.getPassword()))) {
            return R.error("登录失败");
        }

        // 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        // 员工状态 status 0 禁用 1启用
        // 如果员工已被禁用
        if (employee.getStatus() == 0) {
            return R.error("登录失败");
        }

        // 6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",employee.getId());
        return R.success(employee);
    }

    /**
     * 登出接口
     * @param request
     * @return
     */
    @PostMapping("logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("登出成功");
    }
}
