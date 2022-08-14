package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.emtity.Employee;
import com.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 员工登录
 *
 * @author 才
 */
@RestController
@Slf4j
@RequestMapping("employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    /**
     * 登录界面
     * @param request  用于存储对象的session
     * @param employee 前端返回一个json数据，定义来接收
     * @return 返回一个实体类
     */
    @PostMapping("login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //查询数据库是否有该用户
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //如果查出为空
        if (emp==null) {
            return R.error("登录失败");
        }
        //比对密码
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        //查看员工状态
        if(emp.getStatus()==0||emp.getStatus()!=1){
            return R.error("账号已被禁用");
        }
        //登录成功，将员工id存入5Cs5ion并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());

        //准确来说是还有脱敏的
        return R.success(emp);

    }

    /**
     * 用户退出登录
     * @param request  用于删除session
     * @return
     */
    @PostMapping("logout")
    public R<String> logout(HttpServletRequest request){
        //删除session
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }


    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        //设置初始密码，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //创建时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //修改的用户
        //Long empId = (Long) request.getSession().getAttribute("employee");

        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);
        //保存用户
        employeeService.save(employee);
        //返回添加成功
        return R.success("新增员工成功");
    }

    /**
     * 分页查询
     * @param page 前端请求的页数
     * @param pageSize  前端请求的条数
     * @param name  前端搜索框中搜索时携带的参数
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //创建分页构造器
        Page pageInfo = new Page(page,pageSize);

        //创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        //添加名字过滤条件
        //Stirng.Utils是import org.apache.commons.lang.StringUtils这个类，判断name是否为空，不为空则执行
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //根据条件排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        //为什么不用返回的，因为page这个方法会自动帮我们把查询回来的数据封装到pagerInfo里面
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 用户更新数据
     * @param request
     * @param employee
     * @return
     */
    //因为页面只要code,所以只返回code就可以了
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //先获取修改用户的id
        //Long id = (Long) request.getSession().getAttribute("employee");
        //然后修改用户信息
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(id);

        //执行sql语句
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据用户查id
     * @param id
     * @return
     */
    //请求路径employee/${id}
    @GetMapping("/{id}")
    public R<Employee> getByid(@PathVariable Long id){
        log.info("id为：",id);
        //查询id，然后执行
        Employee employee = employeeService.getById(id);
        //判断是否为空
        if(employee==null){
            return R.error("没有查询到对应的员工");
        }
        return R.success(employee);
    }
}
