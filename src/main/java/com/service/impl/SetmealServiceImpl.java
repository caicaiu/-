package com.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CustomException;
import com.dto.SetmealDto;
import com.emtity.Setmeal;
import com.emtity.SetmealDish;
import com.mapper.SetmealMapper;
import com.service.SetmealDishService;
import com.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 套餐保存
     * @param
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐，保存后，id自动响应会setmealDto
        this.save(setmealDto);

        //获取套餐信息,因为套餐信息中没有套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存SetmealDish表，但是有个问题，就是SetmealDish中，没有套餐的id,所以我们把套餐的id加上
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void removeWihDish(List<Long> ids) {
        //先查询套餐表
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //查询套餐的id是否存在和套餐的状态
        queryWrapper.in(ids!=null,Setmeal::getId,ids);
        //
        queryWrapper.eq(Setmeal::getStatus,1);

        //执行sql
        int count = this.count(queryWrapper);
        //如果是启售则不能删除，抛出异常，让全局异常类接收
        if(count>0){
            throw new CustomException("当前套餐在启售中，不能删除");
        }
        //如果都是停售状态
        //删除套餐
        this.removeByIds(ids);
        //删除菜品,菜品的id怎么来呢，从套餐中来，所以我们还有取出菜品id
        //取出菜品id
        LambdaQueryWrapper<SetmealDish> dishqueryWrapper = new LambdaQueryWrapper<>();
        //有多个id值，所以我们直接使用in
        dishqueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(dishqueryWrapper);

    }
}
