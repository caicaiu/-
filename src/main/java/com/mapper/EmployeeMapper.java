package com.mapper;

import com.emtity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 覃江才
* @description 针对表【employee(员工信息)】的数据库操作Mapper
* @createDate 2022-08-01 20:06:54
* @Entity com.emtity.Employee
*/
public interface EmployeeMapper extends BaseMapper<Employee> {

}




