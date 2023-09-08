package com.grazy.Service.impl;

import com.grazy.Service.AuthRoleElementOperationService;
import com.grazy.Service.AuthRoleMenuService;
import com.grazy.Service.AuthRoleService;
import com.grazy.auth.AuthRole;
import com.grazy.auth.AuthRoleElementOperation;
import com.grazy.auth.AuthRoleMenu;
import com.grazy.mapper.AuthRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author: grazy
 * @Date: 2023/9/6 23:58
 * @Description:
 */

@Service
public class AuthRoleServiceImpl implements AuthRoleService {

    @Autowired
    private AuthRoleMapper authRoleMapper;

    //角色-元素操作权限中间表服务
    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    //角色-页面访问权限中间表服务
    @Autowired
    private AuthRoleMenuService authRoleMenuService;


    @Override
    public List<AuthRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIds) {
        return authRoleElementOperationService.getRoleElementOperationByRoleIds(roleIds);
    }


    @Override
    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIds) {
       return authRoleMenuService.getAuthRoleMenusByRoleIds(roleIds);
    }

    @Override
    public AuthRole getRoleByCode(String code) {
        return authRoleMapper.selectRoleByCode(code);
    }
}
