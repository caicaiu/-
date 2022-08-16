package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.common.BaseContext;
import com.common.CustomException;
import com.common.R;
import com.emtity.AddressBook;
import com.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;

    @PostMapping
    public R<String> saveAddressBook(@RequestBody AddressBook addressBook){
        log.info("数据为{}" ,addressBook);
        //获取用户id
        addressBook.setUserId(BaseContext.getId());
        //直接保存
        boolean save = addressBookService.save(addressBook);
        if(save){
            return R.success("添加成功");
        }
        //返回信息
        return R.error("添加失败");

    }

    /**
     * 查询用户的所有地址
     * @return 返回默认地址信息
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        log.info("成功访问");
        //获取用户的id
        Long userid = BaseContext.getId();

        //然后根据id查询数据库
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userid);
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(queryWrapper);
        //然后返回即可
        return R.success(list);
    }

    /**
     * 设置默认地址
     * @param addressBook  获取用户的地址
     * @return  返回默认地址
     */
    @PutMapping("/default")
    public R<AddressBook> putAddressBook(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getId());

        //条件构造器
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(addressBook.getUserId() != null,AddressBook::getUserId,addressBook.getUserId());
        updateWrapper.set(AddressBook::getIsDefault,0);

        //将与用户id所关联的所有地址的is_default字段更新为0
        addressBookService.update(updateWrapper);

        //设置选中的地址为默认值，因为每个地址都有一个默认的id
        addressBook.setIsDefault(1);
        //再将前端传递的地址id的is_default字段更新为1
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        //获取用户id
        Long id = BaseContext.getId();
        //根据用户id查询地址，地址为默认的
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,id);
        queryWrapper.eq(AddressBook::getIsDefault,"1");

        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if(addressBook==null){
            throw  new CustomException("该用户没有用户地址");
        }
        return R.success(addressBook);
    }
}
