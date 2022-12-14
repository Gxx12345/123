package com.itheima.ruji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.ruji.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 信息持久层
 */
@Mapper
public interface IEmployeeMappper extends BaseMapper<Employee> {
}
