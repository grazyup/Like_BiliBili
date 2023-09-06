package com.grazy.Service.impl;

import com.grazy.Service.AuthRoleElementOperationService;
import com.grazy.auth.AuthRoleElementOperation;
import com.grazy.mapper.AuthRoleElementOperationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author: grazy
 * @Date: 2023/9/6 23:57
 * @Description:
 */

@Service
public class AuthRoleElementOperationServiceImpl implements AuthRoleElementOperationService {

    @Autowired
    private AuthRoleElementOperationMapper authRoleElementOperationMapper;

    @Override
    public List<AuthRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIds) {
        //在角色-元素操作权限中间表中联表查询，将权限信息封装到中间表类的属性中
        return authRoleElementOperationMapper.selectAuthElementOperationByRoleIds(roleIds);
    }
}
