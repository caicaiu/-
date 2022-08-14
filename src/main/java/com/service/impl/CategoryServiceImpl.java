package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CustomException;
import com.emtity.Category;
import com.emtity.Dish;
import com.emtity.Setmeal;
import com.mapper.CategoryMapper;
import com.service.CategoryService;
import com.service.DishService;
import com.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    //查看当前要删除的分类id是否与菜品或套餐相关联，若与其中一个关联，则抛出异常
    @Override
    public void remove(Long ids) {
        //创建条件构造条件
        LambdaQueryWrapper<Dish> dishqueryWrapper = new LambdaQueryWrapper();
        dishqueryWrapper.eq(Dish::getCategoryId,ids);
        //查询
        int dishcount = dishService.count(dishqueryWrapper);
        if(dishcount>0){
            //在这里抛出一个异常，那么异常从哪里来呢，我们自定义一个
            throw new CustomException("当前分类包含菜品，您不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealqueryWrapper = new LambdaQueryWrapper();
        setmealqueryWrapper.eq(Setmeal::getCategoryId,ids);
        int setmaelCount = setmealService.count(setmealqueryWrapper);
        if(setmaelCount>0){
            throw new CustomException("当前分类包含菜单，您不能删除");
        }
        //如果都到这里了，发现都没有菜品，然后删除
        super.removeById(ids);
    }

}
