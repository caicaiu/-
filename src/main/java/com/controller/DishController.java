package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.DishDto;
import com.emtity.Category;
import com.emtity.Dish;
import com.emtity.DishFlavor;
import com.service.CategoryService;
import com.service.DishFlavorService;
import com.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     *  新增菜品
     * @return
     */
    @PostMapping
    public R<String> save (@RequestBody  DishDto dishDto){
        //新增菜品前先清理redis中的数据
        //全部清除
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);


        log.info("请求参数为：{}",dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("分页查询执行，page:{},pageSzie:{},name:{}",page,pageSize,name);
        //创建分页
        Page<Dish> pageinfo = new Page<>(page,pageSize);
        //dto对象,不能直接分页，因为数据库中并没有这个字段，所以我们要想有值，就需要拷贝
        Page<DishDto> dishDtoPage = new Page<>();
        //创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //执行条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //进行排序
        queryWrapper.orderByAsc(Dish::getUpdateTime);
        //进行分页
        dishService.page(pageinfo,queryWrapper);

        //因为这里执行后，pageinfo就有值了，所以我们可以直接拷贝
        //把record忽略掉，因为他是要进行处理的，所以我们把他忽略掉
        BeanUtils.copyProperties(pageinfo,dishDtoPage,"records");
        //获取Dish的records
        List<Dish> dishList = pageinfo.getRecords();
        //对集合中的每一个元素进行处理,然后使用一个对象来接收
        List<DishDto> dishDtoList = dishList.stream().map((item->{
            //创建一个dto对象
            DishDto dishDto = new DishDto();
            //拷贝
            BeanUtils.copyProperties(item,dishDto);
            //获取菜品的id
            Long categoryId = item.getCategoryId();
            //通过id查询菜品，我们注入一个categoryServcie
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //然后把赋值给dto
                dishDto.setCategoryName(category.getName());
            }
            //返回数据
            return dishDto;
        })).collect(Collectors.toList());

        //把他赋给dto分页中
        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);
    }

    /**
     * 获取菜品的数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        log.info("id值为：{}",id);
        //DishDto要查的是两张表，所以我们在DishService定义一个方法
        DishDto dishDto = dishService.getWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     *  修改菜品
     * @return
     */
    @PutMapping
    public R<String> update (@RequestBody  DishDto dishDto){
        //全部删除
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //只删除相对应的
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        //因为要保存两张表，所以我们在DishSerciec定义一个方法

        log.info("请求参数为：{}",dishDto);

        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }


    /**
     * 查询菜品包含菜品口味和菜名字
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        List<DishDto> dtoList = null;
        //设置key,有菜品的id进行拼接
        String key = "dish_"+dish.getCategoryId()+"_1";

        //查询的时候先查询redis
        dtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果数据存在，那么就直接返回
        if(dtoList!=null){
            return R.success(dtoList);
        }
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //查询id
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（1为起售，0为停售）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        List<Dish> list = dishService.list(queryWrapper);
        //创建DishDto对象
        dtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            //拷贝
            BeanUtils.copyProperties(item,dishDto);

            //获取菜品id
            Long categoryId = item.getCategoryId();
            //根据菜品查询id
            LambdaQueryWrapper<Category> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(categoryId!=null,Category::getId,categoryId);
            Category crategoryid = categoryService.getOne(queryWrapper1);
            //获取名字
            if(crategoryid!=null){
                //把他设置进item中
                dishDto.setCategoryName(crategoryid.getName());
            }
            //然后这里再设置他的菜品,查询他的口味
            Long itemId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            //根据菜品id查询
            lambdaQueryWrapper.eq(itemId!=null,DishFlavor::getDishId,itemId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(lambdaQueryWrapper);
            //设置到dto里面去
            dishDto.setFlavors(dishFlavors);
            //然后返回dishDto
            return dishDto;
        }).collect(Collectors.toList());
        //如果数据不存在，那么就查询，然后存到redis中
        redisTemplate.opsForValue().set(key,dtoList,60, TimeUnit.MINUTES);
        //添加排序条件
        return R.success(dtoList);
    }


}
