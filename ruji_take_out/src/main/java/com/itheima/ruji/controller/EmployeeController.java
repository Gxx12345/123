package com.itheima.ruji.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.R;
import com.itheima.ruji.entity.Employee;
import com.itheima.ruji.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
     * 登录条件
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employeeParam, HttpServletRequest request) {
        //前后端调练
        log.info("employee---->", employeeParam.toString());
        //用户名
        //判断此数据库中名字和密码是否为空
        if(StringUtils.isBlank(employeeParam.getUsername())||StringUtils.isBlank(employeeParam.getPassword())){
            return  R.error("登录失败");
        }
        String username = employeeParam.getUsername();
        //1.根据页面提交的用户名username 查询数据库
        //拼接查询条件
        LambdaQueryWrapper<Employee> queryChainWrapper = new LambdaQueryWrapper<>();
        //查询用户名在这个员工表是否存在
        //select * from Employee where username="username"
        //下面这个是利用lamada中的方法引用  c从Employee中取出username 去查找前端传来的username
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
        return R.success("登出成功");
    }

    /**
     * 添加员工
     * @param employeeParam
     * @param request
     * @return
     */
    @PostMapping
    public R<String>save(@RequestBody Employee employeeParam, HttpServletRequest request){
        //前后端调练
        log.info("新增员工，员工信息：{}",employeeParam.toString());
        log.info("controller当前线程的id:{}",Thread.currentThread().getName());
        //设置初始密码并赋值
         employeeParam.setPassword( DigestUtils.md5DigestAsHex("123456".getBytes()));
        //region 折叠添加员工信息
        LocalDateTime now =  LocalDateTime.now();
        employeeParam.setCreateTime(now);
        employeeParam.setUpdateTime(now);
        //从seeion中获取员工ID
        //获得当前用户的id
        Long attribute = (Long) request.getSession().getAttribute(AntPathmathcherSS.EN_V587);
        //添加人
        employeeParam.setCreateUser(attribute);
        //修改人
        employeeParam.setUpdateUser(attribute);
        //endregion
        //保存业务
        iEmployeeService.save(employeeParam);
        return R.success("添加成功");
    }

    /**
     * 分页条件查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page,int pageSize,String name) {
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);
        //当前页
        //当前要显示多少行
        //分页page对象
            Page<Employee> pageInfo=new Page<>();
            //当前页
        pageInfo.setCurrent(page);
        //当前行
        pageInfo.setSize(pageSize);
        //使用map条件 ,拼接时候使用wrapper
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //拼接name条件
        //当传入的名字不为空,返回true ,完了进入判断语句在进行查询
            if (StringUtils.isNotBlank(name)) {
                wrapper.like(Employee::getName,name);
            }
        //添加排序条件
        //更新时间降序
        wrapper.orderByDesc(Employee::getUpdateTime);
        Page <Employee>page1 = iEmployeeService.page(pageInfo, wrapper);
        return R.success(page1);
    }

    /**
     * 修改状态
     * @param employeeParam
     * @return
     */
    @PutMapping
    public R<String>update(@RequestBody Employee employeeParam,HttpServletRequest httpServletRequest ){
        log.info("前后联通:{}",employeeParam.toString());
        //在session取到当前y用户的id
        Long attribute = (Long) httpServletRequest.getSession().getAttribute(AntPathmathcherSS.EN_V587);
        if (attribute==null){
            R.error("拜拜");
        }
        //region 修改状态信息折叠
        LocalDateTime now =  LocalDateTime.now();
        employeeParam.setUpdateTime(now);
        employeeParam.setUpdateUser(attribute);
        //endregion
        iEmployeeService.updateById(employeeParam);
        // js 只能处理16位以内的数字,如果超过16位,会发生进制,那么会丢失精度
        // 返回的id 是 数字是超过16位的
        // 把返回的id 变成字符串的话可以不用进制了
        // 我们可以通过使用转换器的方式,来改变我们要返回的值以及值的类型
        // DispatcherServlet
        return R.success("修改成功");
    }


    @GetMapping("/{id}")
    public R<Employee>update(@PathVariable Long id){
        if(id==null){
            R.error("拜拜");
        }
        Employee byId = iEmployeeService.getById(id);
        if(byId==null){
            R.error("拜拜");
        }
       return   R.success(byId);
    }
}

