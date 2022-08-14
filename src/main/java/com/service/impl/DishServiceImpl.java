package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dto.DishDto;
import com.emtity.Dish;
import com.emtity.DishFlavor;
import com.mapper.DishMapper;
import com.service.DishFlavorService;
import com.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    

    /**
     * 保存菜品和口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //首先保存一个菜品，因为他继承了Dish，所以可以直接传
        this.save(dishDto);

        //然后保存一个口味，我们查看前端发现，如果这样写的话，那么菜品的id保存不上
        //dishFlavorService.saveBatch(dishDto.getFlavors());
        //我们可以遍历集合中的每一个元素，然后把菜品id附上去
        //菜品id
        Long id = dishDto.getId();
        //获取所有口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //遍历list中每一个元素，然后添加上菜品id
        flavors = flavors.stream().map((item)->{
            item.setDishId(id);
            return item;
        } ).collect(Collectors.toList());

        //保存菜品
        boolean b = dishFlavorService.saveBatch(flavors);

     
    }

    /**
     * 获取菜品和id
     * @param id
     * @return
     */
    @Override
    public DishDto getWithFlavor(Long id) {
        //查询Dish表
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        //把dish拷贝给他
        BeanUtils.copyProperties(dish,dishDto);
        //查询Flavor表，根据dish中的id查询
        //创建一个条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        //执行方法
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        //创建一个dishdto然后赋值给他
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 修改菜品和口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //修改dish
        this.updateById(dishDto);
        //清理之前的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //查询所有口味
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        //然后删除
        dishFlavorService.remove(queryWrapper);
        //保存现在修改的口味
        //获取口味，里面只保存了口味，并没有保存id,所以我们把id写入到里面去
        List<DishFlavor> dtoFlavors = dishDto.getFlavors();
        dtoFlavors = dtoFlavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }) .collect(Collectors.toList());
        //然后保存
        dishFlavorService.saveBatch(dtoFlavors);
    }


}
