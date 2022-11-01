package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.entity.Employee;
import com.itheima.ruji.mapper.IEmployeeMappper;
import com.itheima.ruji.service.IEmployeeService;
import org.springframework.stereotype.Service;

/**
 * 信息表的业务层实现类
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<IEmployeeMappper, Employee>implements IEmployeeService {
}
