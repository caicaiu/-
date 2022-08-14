package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.SetmealDto;
import com.emtity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 保存套餐
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐
     * @param ids
     */
    void removeWihDish(List<Long> ids);
}
