package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
    private IEmployeeService employeeService;

    /**
     * 登录
     * @param employeeParm
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employeeParm, HttpServletRequest request){
        //前后端互联，前端传入的一切参数都是不可信的，要进行参数的判断
        if(StringUtils.isBlank(employeeParm.getUsername())||
            StringUtils.isBlank(employeeParm.getPassword())){
            return R.error("登陆失败");
        }

        //1、将页面提交的密码password进行md5加密处理
        String password = employeeParm.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        String username = employeeParm.getUsername();
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,username);
        Employee employee = this.employeeService.getOne(queryWrapper);


        if(employee==null){
            return R.error("用户不存在");
        }
        if(!password.equals(employee.getPassword())){
            return R.error("用户名或密码错误");
        }
        if(employee.getStatus()==0){
            return R.error("账号已被封禁");
        }
        request.getSession().setAttribute(GlobalConstant.EMPLOYEE_KEY,employee.getId());
        return R.success(employee);
    }

    /**
     * 员工退出
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employeeParam,HttpServletRequest request){
        //1.请求打印
        log.info("前后端联通");
        log.info("employee ==> {}",employeeParam.toString());

        //设置初始密码(MD5加密)
        String initPassword = DigestUtils.md5DigestAsHex("123456".getBytes());
        employeeParam.setPassword(initPassword);

        //设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        employeeParam.setCreateTime(now);
        employeeParam.setUpdateTime(now);

        //从session中获取用户ID
        //并设置创建、更新用户ID值
        //当前登录用户ID
        Long employeeId = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        //创建人
        employeeParam.setCreateUser(employeeId);
        //修改人
        employeeParam.setUpdateUser(employeeId);
        //保存
        this.employeeService.save(employeeParam);
        return R.success("保存成功");
    }
    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page,Integer pageSize,String name){
        log.info("分页查询前后端联通");
        //分页pag对象
        Page<Employee> queryPage = new Page<>();
        //当前页
        queryPage.setCurrent(page);
        //当前页行数
        queryPage.setSize(pageSize);
        //mybatisplus 条件封装wrapper
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //如果前端传入name条件，才加入name
        if(StringUtils.isNotBlank(name)){
            queryWrapper.like(Employee::getName, name);
        }

        //更新时间倒叙
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //查询分页
        Page<Employee> result = this.employeeService.page(queryPage, queryWrapper);
        return R.success(result);
    }
}
