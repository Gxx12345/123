package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.entity.Employee;
import com.alibaba.reggie.mapper.EmployeeMapper;
import com.alibaba.reggie.service.IEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
}
