package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.emtity.Category;
import com.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * 新增菜品和套餐
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功");
    }

    /**
     * 分页功能
     * @param page  当前页数
     * @param pageSize 当前页数的显示数据
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //创建一个page对象
        Page<Category> pageinfo = new Page<>(page,pageSize);
        //创建一个构造函数，因为要进行排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //以sort方式进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //执行函数
        categoryService.page(pageinfo,queryWrapper);
        return R.success(pageinfo);
    }

    /**
     * 根据id来删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> put(@RequestBody Category category){
        log.info("修改的文件为{}",category);
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 下拉框回显
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        log.info("请求的类型为{}",category);
        //创建一个条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //进行排序
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getCreateTime);
        //查询条件
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
