package com.grazy.Service;

import com.grazy.auth.AuthRoleElementOperation;

import java.util.List;
import java.util.Set;

/**
 * @Author: grazy
 * @Date: 2023/9/6 23:56
 * @Description: 角色-元素操作权限中间表服务层
 */
public interface AuthRoleElementOperationService {

    /** 联表查询元素操作权限
     * @param roleIds 角色id
     * @return 封装好权限信息的角色-元素操作中间表类集合
     */
    List<AuthRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIds);
}
