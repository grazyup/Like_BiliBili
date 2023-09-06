package com.grazy.Service.impl;

import com.grazy.Service.UserRoleService;
import com.grazy.auth.UserRole;
import com.grazy.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<UserRole> getUserRoleDate(Long currentUserId) {
        return userRoleMapper.selectUserRoleDateByUserId(currentUserId);
    }

}
