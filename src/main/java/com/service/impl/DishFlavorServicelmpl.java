package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emtity.DishFlavor;
import com.mapper.DishFlavorMapper;
import com.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServicelmpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}
