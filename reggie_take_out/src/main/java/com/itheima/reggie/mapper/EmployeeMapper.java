package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    // 增删改查
    // mybatisplus 他是把一些常用的对于数据库来说,表的相关的操作,比如增删改查
    // 注入这个mapper,autowried.调用其中的某些方法,就能够达到不写sql的情况下,就能对数据库进行操作
}
