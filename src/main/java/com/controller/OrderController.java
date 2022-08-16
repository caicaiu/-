package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.BaseContext;
import com.common.R;
import com.emtity.OrderDetail;
import com.emtity.Orders;
import com.service.OrderDetailService;
import com.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrdersService ordersService;

    @Autowired
    OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order){
        log.info("成功访问数据：{}",order);
        //添加一个方法
        ordersService.submit(order);

        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        //获取用户id
        Long userId = BaseContext.getId();
        //根据用户id查询订单号
        //使用分页
        Page<OrderDetail> pageinfo = new Page<>(page,pageSize);
        //查询订单表
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //根据用户id查询
        queryWrapper.eq(Orders::getUserId,userId);
        //排序
        List<Orders> serviceOne = ordersService.list(queryWrapper);
        //一次获得用户订单
        for (Orders orders:serviceOne) {
            Long ordersId = orders.getId();
        }
        //查询订单详情
        LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(OrderDetail::getOrderId,orderId);
        //查询用户id
        orderDetailService.page(pageinfo,queryWrapper1);
        //返回分页数据
        return R.success(pageinfo);
    }


}
