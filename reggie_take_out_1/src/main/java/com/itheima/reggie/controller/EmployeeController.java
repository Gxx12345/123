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
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * 员工控制器
 *
 * @author t3rik
 * @since 2022/9/23 16:26
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    // controller -> service -> mapper
    @Autowired
    private IEmployeeService employeeService;

    /**
     * 登录的接口
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employeeParam, HttpServletRequest servletRequest) {
        // 前后端联通
        // 前端传入的一切参数都是不可信的.
        log.info("employee ==> {}", employeeParam.toString());
        // 用户名和密码这两个参数,是必须要有的.
        // 没有这两个参数,我们这整个的登录接口都没有意义
        if (StringUtils.isBlank(employeeParam.getUsername()) ||
                StringUtils.isBlank(employeeParam.getPassword())) {
            return R.error("登录失败");
        }
        // 用户名
        String username = employeeParam.getUsername();
        // 1、根据页面提交的用户名username查询数据库
        // 拼接一些查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 查询用户名在这个员工表中是否存在
        // select * from Employee where username = 'username'
        queryWrapper.eq(Employee::getUsername, username);
        // 根据这个查询条件,查询一条记录
        Employee employee = this.employeeService.getOne(queryWrapper);
        // 2、如果没有查询到则返回登录失败结果
        // 如果是空,就代表这个用户在数据库中不存在
        if (employee == null) {
            return R.error("登录失败");
        }
        // 3、将页面提交的密码password进行md5加密处理
        String password = employeeParam.getPassword();
        // 这个是可以进行md5加密的工具类,spring
        String md5password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 4、密码比对，如果不一致则返回登录失败结果
        // 与数据库中的密码进行比对
        if (!(md5password.equals(employee.getPassword()))) {
            // 如果不一致
            return R.error("登录失败");
        }
        // 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        // 员工状态 status 0 禁用 1启用
        // 如果员工已被禁用
        if (employee.getStatus() == 0) {
            return R.error("登录失败");
        }
        // 6、登录成功，将员工id存入Session并返回登录成功结果
        servletRequest.getSession().setAttribute(GlobalConstant.EMPLOYEE_KEY, employee.getId());
        return R.success(employee);
    }

    /**
     * 登出接口
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute(GlobalConstant.EMPLOYEE_KEY);
        return R.success("登出成功");
    }

    // 如果说要接收前端请求的话
    // 要有请求方式
    // 还要有请求地址
    // 还需要接收参数

    @PostMapping
    public R<String> save(@RequestBody Employee employeeParam, HttpServletRequest request) {
        // 1.打印请求数据
        log.info("前后端联通");
        log.info("employee ===> {}", employeeParam.toString());
        // 2.设置初始密码(MD5加密)
        String initPassword = DigestUtils.md5DigestAsHex("123456".getBytes());
        // 赋值初始密码
        employeeParam.setPassword(initPassword);
        // 3.设置创建和更新时间
        // 12.19
        //region 注释掉的代码
        //        LocalDateTime now = LocalDateTime.now();
//        // 创建时间
//        employeeParam.setCreateTime(now);
//        // 修改时间
//        employeeParam.setUpdateTime(now);
        // 4.从session中获取员工ID，
        // 并设置创建、更新用户Id
        // 当前登录用户的ID
//        Long employeeId = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
//        // 创建人
//        employeeParam.setCreateUser(employeeId);
//        // 修改人
//        employeeParam.setUpdateUser(employeeId);
        //endregion
        // 5.完成保存业务 // service -> mapper
//        log.info("Controller中:当前线程ID ===> {}", Thread.currentThread().getId());
        this.employeeService.save(employeeParam);
        return R.success("保存成功");
    }


    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page, Integer pageSize, String name) {
        log.info("前后端联通");
        // 对于分页来说
        // 当前页 页码
        // 当前页要显示多少行
        // 分页page对象
        Page<Employee> queryPage = new Page<>();
        // 当前页
        queryPage.setCurrent(page);
        // 当前页要显示多少行
        queryPage.setSize(pageSize);
        // 使用mp的话,条件拼装要记得使用wrapper
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 拼接name条件
        // 如果前端传入了name的话,我们才把name当做条件,拼接到sql中
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(Employee::getName, name);
        }
        // 升序 0 - 10
        // 倒序 10 - 0
        // 更新时间倒序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 查询分页
        Page<Employee> result = this.employeeService.page(queryPage, queryWrapper);
        return R.success(result);
    }

    /**
     * 启用禁用以及更新操作
     * @param employeeParam
     * @param request
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employeeParam, HttpServletRequest request) {
        log.info("前后端联通");
        // 更新时间
        employeeParam.setUpdateTime(LocalDateTime.now());
        // 在session取到当前登录用户的id
        Long employeeId = (Long)request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        // 更新人
        employeeParam.setUpdateUser(employeeId);
        // 更新操作
        this.employeeService.updateById(employeeParam);
        // js 只能处理16位以内的数字,如果超过16位,那么会丢失精度
        // 返回的id 是 数字
        // 把返回的id 变成字符串的话.
        // 我们可以通过使用转换器的方式,来改变我们要返回的值以及值的类型
        // DispatcherServlet
        return R.success("操作成功");
    }


    /**
     * 根据员工id获取信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        if(id == null){
            return R.error("传入的参数有误");
        }
        Employee employee = this.employeeService.getById(id);
        if(employee == null){
            return R.error("传入的参数有误");
        }
        return R.success(employee);
    }
}
