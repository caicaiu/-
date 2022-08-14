package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.SetmealDto;
import com.emtity.Category;
import com.emtity.Setmeal;
import com.service.CategoryService;
import com.service.SetmealDishService;
import com.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 保存套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String>  save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐");
        //因为要设计两张表，所以我们在SetmealServic中编写一个方法
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("成功访问,page:{},pageSize:{},name:{}",page,pageSize,name);
        //创建page对象，Setmeal中没有菜品分类，所以我们在dto中添加一个菜品的名字
        Page<Setmeal> pageinfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //条件查询name
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //执行sql语句
        setmealService.page(pageinfo,queryWrapper);
        //把setmeal中的除了records给SetmealDto，因为records还有查询菜品
        BeanUtils.copyProperties(pageinfo,dtoPage,"records");
        //获取records，然后把id变为name
        List<Setmeal> setmeals = pageinfo.getRecords();
        //然后修改里面的数据
        List<SetmealDto> setmealDtoList = setmeals.stream().map((item)->{
            //创建一个Dto对象
            SetmealDto setmealDto = new SetmealDto();
            //把item的数据拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //先获取菜品的id,然后查询出来数据，再赋值给Dto
            Long categoryId = item.getCategoryId();
            //根据id查询数据库
            Category category = categoryService.getById(categoryId);
            //判断他是为空
            if(category!=null){
                //菜品名字
                String categoryName = category.getName();
                //修改dto中的值
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //然后把返回的records赋给dto对象
        dtoPage.setRecords(setmealDtoList);
        //返回的结果返回
        return R.success(dtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long > ids){
        log.info("成功删除{}",ids);
        setmealService.removeWihDish(ids);
        return null;
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("成功访问");
        Long categoryId = setmeal.getCategoryId();
        //展示业务套餐，查询数据库，状态为1的
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //展示状态为1，菜品为传过来数据的
        queryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId);
        //状态为1
        queryWrapper.eq(Setmeal::getStatus,"1");
        //根据修改时间排序
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);
        //查询一个list集合
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

}
