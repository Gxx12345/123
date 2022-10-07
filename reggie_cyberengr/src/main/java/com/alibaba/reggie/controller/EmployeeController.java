package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.entity.Employee;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.service.IEmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Api(tags = "员工控制器")
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private IEmployeeService service;

    /**
     * 员工登录
     *
     * @param employeeParam
     * @param request
     * @return
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<Employee> login(@RequestBody Employee employeeParam, HttpServletRequest request) {
        //判断用户名和密码是否为空
        if (StringUtils.isBlank(employeeParam.getUsername()) || StringUtils.isBlank(employeeParam.getPassword())) {
            return Result.error("登录失败!");
        }
        //姓名去除前后空格
        employeeParam.setUsername(employeeParam.getUsername().trim());
        //根据username查询用户信息
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employeeParam.getUsername());
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
        request.getSession().setAttribute(GlobalConstant.EMPLOYEE_KEY, employee.getId());
        return Result.success(employee);
    }

    /**
     * 员工登出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        //移除session值
        request.getSession().removeAttribute(GlobalConstant.EMPLOYEE_KEY);
        return Result.success("退出成功！");
    }

    /**
     * 添加新员工
     *
     * @param employeeParam
     * @param request
     * @return
     */
    @PostMapping
    public Result<String> addEmployee(@RequestBody Employee employeeParam, HttpServletRequest request) {

        employeeParam.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeParam.setStatus((short) 1);
        //region 公共字段填充
        /*LocalDateTime now = LocalDateTime.now();
        employeeParam.setCreateTime(now);
        employeeParam.setUpdateTime(now);

        Long employeeId = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        employeeParam.setCreateUser(employeeId);
        employeeParam.setUpdateUser(employeeId);*/
        //endregion
        boolean save = service.save(employeeParam);
        return save ? Result.success(GlobalConstant.FINISHED) : Result.error(GlobalConstant.FAILED);
    }

    /**
     * 员工的分页查询
     *
     * @param page     当前页数
     * @param pageSize 每页的大小
     * @param name     查询的员工名称
     * @return
     */
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页数",required = true),
            @ApiImplicitParam(name = "pageSize",value = "分页大小",required = true),
            @ApiImplicitParam(name = "name",value = "名字关键字",required = false)
    })
    @GetMapping("/page")
    public Result<Page<Employee>> pageResult(Long page, Long pageSize, String name) {
        if (page == null || pageSize == null) {
            return Result.error(null);
        }
        //mybatisplus的分页插件
        Page<Employee> employeePage = new Page<>();
        employeePage.setCurrent(page);
        employeePage.setSize(pageSize);
        //mybatisplus的查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), Employee::getName, name)
                .orderByDesc(Employee::getUpdateTime);
        //进行分页查询
        service.page(employeePage, queryWrapper);
        return Result.success(employeePage);
    }

    /**
     * 根据id更新员工
     *
     * @param employeeParam
     * @param request
     * @return
     */
    @PutMapping
    public Result<String> updateStatus(@RequestBody Employee employeeParam, HttpServletRequest request) {
        //region 公共字段填充
        /*employeeParam.setUpdateTime(LocalDateTime.now());
        Long id = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        employeeParam.setUpdateUser(id);*/
        //endregion
        Long attribute = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        if (employeeParam.getId() == null || employeeParam.getId() == attribute) {
            return Result.error(GlobalConstant.FAILED);
        }
        boolean update = service.updateById(employeeParam);
        return update ? Result.success(GlobalConstant.FINISHED) : Result.error(GlobalConstant.FAILED);
    }

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> selectById(@PathVariable Long id) {
        Employee employee = null;
        if (id == null || (employee = service.getById(id)) == null) {
            return Result.error(GlobalConstant.FAILED);
        }
        return Result.success(employee);
    }
}
