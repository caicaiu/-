package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emtity.User;
import com.mapper.UserMapper;
import com.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserSerciceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
