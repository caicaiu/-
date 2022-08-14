package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emtity.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 根据用户id删除
     * @param ids
     */
    public void remove(Long ids);
}
