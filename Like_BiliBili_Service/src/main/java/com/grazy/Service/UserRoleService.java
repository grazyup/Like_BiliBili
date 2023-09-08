package com.grazy.Service;

import com.grazy.auth.UserRole;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/6 20:40
 * @Description: 用户-角色中间表服务层
 */

public interface UserRoleService {

    /**
     * 根据用户id获取用户角色关联表中的数据
     * @param currentUserId 当前用户id
     * @return 用户角色关联数据
     */
    List<UserRole> getUserRoleDate(Long currentUserId);


    /**
     * 新增用户角色关联数据
     * @param id 新增的用户id
     */
    void addUserRoleData(Long id);
}
