package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.BaseContext;
import com.common.R;
import com.emtity.ShoppingCart;
import com.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> save(@RequestBody ShoppingCart shoppingCart){
        log.info("成功访问");
        //获得用户id
        Long userid = BaseContext.getId();
        shoppingCart.setUserId(userid);
        //判断他是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        //查询数据，然后存到shopping中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        //如果他为空，那么就代表是套餐id
        if(dishId!=null){
            //菜品,查询shopping中是否拥有dishID
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //然后查询
        ShoppingCart serviceOne = shoppingCartService.getOne(queryWrapper);

        if(serviceOne!=null){
            //如果存在那么number就加1
            serviceOne.setNumber(serviceOne.getNumber()+1);
            //这个应该是修改
            shoppingCartService.updateById(serviceOne);
        }else {
            //不存在就那么就设置为1
            serviceOne = shoppingCart;
            serviceOne.setNumber(1);
            //这个是保存
            shoppingCartService.save(serviceOne);
        }

        //返回给前端
        return R.success(serviceOne);
    }

    /**
     * 获取用户的所有信息
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("成功访问");
        //获取用户的id
        Long userId = BaseContext.getId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        //根据id查询shopping数据库
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        log.info("清空数据");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getId());
        shoppingCartService.remove(queryWrapper);
        return R.success("成功删除");
    }


}
