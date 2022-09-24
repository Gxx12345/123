package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")

public class EmployeeController {

    @Autowired
    private IEmployeeService iEmployeeService;


    /**
     * 员工登录
     * @param request Servlet 请求
     * @param employeeParam 前端传来的对象
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employeeParam, HttpServletRequest request) {

        //将页面提交的密码进行md5加密处理
        String password = employeeParam.getPassword();
        password =  DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employeeParam.getUsername());
        Employee emp = iEmployeeService.getOne(queryWrapper);

        //如果没有查询到则返回登陆失败结果
        if (emp == null) {
            return R.error("登陆失败");
        }

        //密码比对，如果不一致则返回登陆失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }

        //查看员工状态，如果账号为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //登陆成功，将员工id存入Session并返回登陆成功结果
        request.getSession().setAttribute(GlobalConstant.EMPLOYEE_KEY,emp.getId());
        return R.success(emp);
    }


    /**
     * 员工退出
     * @param request Servlet 请求
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute(GlobalConstant.EMPLOYEE_KEY);
        return R.success("退出成功");
    }


    /**
     * 添加员工
     * @param employeeParam 前端传来的对象
     * @param request Servlet 请求
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employeeParam , HttpServletRequest request) {
        //前后端联通
        log.info("emp :{}",employeeParam);

        //将默认密码进行md5加密
        String md5PassWord = DigestUtils.md5DigestAsHex("123456".getBytes());
        //设置新添加员工的密码
        employeeParam.setPassword(md5PassWord);

        //设置当前创建表和更新表的时间 --- 统一相同时间
        LocalDateTime now = LocalDateTime.now();
        employeeParam.setCreateTime(now);
        employeeParam.setUpdateTime(now);

        //获取当前登录用户的id（值）
        Long empId = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        //设置创建用户
        employeeParam.setCreateUser(empId);
        //设置修改用户
        employeeParam.setUpdateUser(empId);

        //使用Service接口调用mybatis-plus中的方法
        iEmployeeService.save(employeeParam);

        return R.success("新增员工成功");
    }


    /**
     * 分页查询
     * @param page 当前查询页码
     * @param pageSize 每页展示记录数
     * @param name 查询条件
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page,int pageSize,String name) {
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //筛选条件
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(Employee::getName,name);
        }
        //按修改时间倒序排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        Page<Employee> result = iEmployeeService.page(pageInfo, queryWrapper);
        return R.success(result);
    }


    /**
     * 根据id修改员工信息
     * @param employeeParam 前端传来的对象
     * @param request Servlet 请求
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employeeParam , HttpServletRequest request) {

        /*
            因为js无法包装16位数的数据，因此数据会丢失精度
            调用mvc中的对象映射器jacksonObjectMapper ：基于jackson将Java对象转为json，或者将json转为Java对象
         */

        Long employeeId = (Long)request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        //更新最后操作时间
        employeeParam.setUpdateTime(LocalDateTime.now());
        //更新最后操作人
        employeeParam.setUpdateUser(employeeId);
        //调用mybatis-plus中的方法修改对象信息
        iEmployeeService.updateById(employeeParam);

        return R.success("修改成功");
    }


    /**
     * 根据id查询员工信息 并进行回显
     * @param id 前端传来的id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        if (id == null) {
            return R.error("传入的参数有误");
        }
        Employee byId = iEmployeeService.getById(id);
        if (byId == null) {
            return R.error("传入的参数有误");
        }
        return R.success(byId);
    }

}
