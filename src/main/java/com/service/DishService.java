package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.DishDto;
import com.emtity.Dish;

public interface DishService extends IService<Dish>{

    /**
     * 保存菜品和口味
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 获取菜品和口味
     * @param id
     * @return
     */
    DishDto getWithFlavor(Long id);

    /**
     * 修改菜品和口味
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);
}
