package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emtity.ShoppingCart;
import com.mapper.ShoppingCartMaapper;
import com.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartImpl extends ServiceImpl<ShoppingCartMaapper, ShoppingCart>
        implements ShoppingCartService {
}
