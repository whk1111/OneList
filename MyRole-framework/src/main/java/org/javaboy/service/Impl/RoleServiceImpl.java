package org.javaboy.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.javaboy.domain.Role;
import org.javaboy.mapper.RoleMapper;
import org.javaboy.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: WangHongKun
 * @Date: 2022/9/22 16:58
 * @Email: 2028911483@qq.com
 * @Phone: 18683977706
 */

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private  RoleMapper roleMapper;
    @Override
    public Integer insertSelective(Role role) {
        Integer id = roleMapper.insertSelective(role);
        return id;
    }
}
