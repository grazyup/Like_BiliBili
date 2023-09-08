package com.grazy.Service.impl;

import com.grazy.Common.AuthRoleConstant;
import com.grazy.Service.AuthRoleService;
import com.grazy.Service.UserRoleService;
import com.grazy.auth.AuthRole;
import com.grazy.auth.UserRole;
import com.grazy.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/6 20:41
 * @Description:
 */

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private AuthRoleService authRoleService;

    @Override
    public List<UserRole> getUserRoleDate(Long currentUserId) {
        return userRoleMapper.selectUserRoleDateByUserId(currentUserId);
    }

    @Override
    public void addUserRoleData(Long id) {
        //根据角色编码code获取角色对象
        AuthRole authRole = authRoleService.getRoleByCode(AuthRoleConstant.ROLE_LV0);
        //新的用户角色关系类
        UserRole userRole = new UserRole(null,id,authRole.getId(),new Date(),null,null);
        userRoleMapper.insertUserRoleData(userRole);
    }
}
