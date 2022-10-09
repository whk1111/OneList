package org.javaboy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaboy.domain.Role;

/**
 * @Author: WangHongKun
 * @Date: 2022/9/22 16:58
 * @Email: 2028911483@qq.com
 * @Phone: 18683977706
 */


public interface RoleService  extends IService<Role> {

    public Integer insertSelective(Role role);
}
