package com.grazy.Service.impl;

import com.grazy.Service.AuthRoleMenuService;
import com.grazy.auth.AuthRoleMenu;
import com.grazy.mapper.AuthRoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author: grazy
 * @Date: 2023/9/7 1:00
 * @Description:
 */

@Service
public class AuthRoleMenuServiceImpl implements AuthRoleMenuService {

    @Autowired
    private AuthRoleMenuMapper authRoleMenuMapper;

    @Override
    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIds) {
        //在角色-访问页面权限中间表中联表查询，将权限信息封装到中间表类的属性中
        return authRoleMenuMapper.selectAuthRoleMenusByRoleIds(roleIds);
    }

}
