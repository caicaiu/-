package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emtity.Employee;
import com.service.EmployeeService;
import com.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
* @author 覃江才
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2022-08-01 20:06:54
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService{

}




