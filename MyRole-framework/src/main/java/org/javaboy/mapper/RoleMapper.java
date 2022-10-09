package org.javaboy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.javaboy.domain.Role;

/**
 * @Author: WangHongKun
 * @Date: 2022/9/22 16:58
 * @Email: 2028911483@qq.com
 * @Phone: 18683977706
 */


public interface RoleMapper extends BaseMapper<Role> {

  public Integer insertSelective( Role role);
}
