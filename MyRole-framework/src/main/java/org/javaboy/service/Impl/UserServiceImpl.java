package org.javaboy.service.Impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Verify;
import org.javaboy.domain.Role;
import org.javaboy.domain.User;
import org.javaboy.mapper.UserMapper;
import org.javaboy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: WangHongKun
 * @Date: 2022/9/22 16:58
 * @Email: 2028911483@qq.com
 * @Phone: 18683977706
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
    @Autowired
    private  UserMapper userMapper;





}
