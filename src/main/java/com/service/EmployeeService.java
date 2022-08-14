package com.service;

import com.emtity.Employee;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

/**
* @author 覃江才
* @description 针对表【employee(员工信息)】的数据库操作Service
* @createDate 2022-08-01 20:06:54
*/
@Service
public interface EmployeeService extends IService<Employee> {

}
