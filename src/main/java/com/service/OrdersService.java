package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emtity.Orders;

public interface OrdersService extends IService<Orders> {

    void submit(Orders order);
}
