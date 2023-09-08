package com.grazy.Service.impl;

import com.grazy.Service.AuthRoleService;
import com.grazy.Service.UserAuthoritiesService;
import com.grazy.Service.UserRoleService;
import com.grazy.auth.AuthRoleElementOperation;
import com.grazy.auth.AuthRoleMenu;
import com.grazy.auth.UserAuthorities;
import com.grazy.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2023/9/6 20:36
 * @Description:
 */

@Service
public class UserAuthoritiesServiceImpl implements UserAuthoritiesService {

    //用户-角色关联表服务
    @Autowired
    private UserRoleService userRoleService;

    //角色表服务
    @Autowired
    private AuthRoleService authRoleService;

    @Override
    public UserAuthorities getUserAuthorities(Long currentUserId) {
        //根据用户id在用户角色表中获取角色信息
        List<UserRole> userRoleList = userRoleService.getUserRoleDate(currentUserId);
        //提取角色id集合
        Set<Long> roleIds = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        //获取角色的页面元素操作权限
        List<AuthRoleElementOperation> roleElementOperationList = authRoleService.getRoleElementOperationByRoleIds(roleIds);
        //获取用户的页面访问权限
        List<AuthRoleMenu> authRoleMenuList = authRoleService.getAuthRoleMenusByRoleIds(roleIds);
        return new UserAuthorities(roleElementOperationList,authRoleMenuList);
    }
}
