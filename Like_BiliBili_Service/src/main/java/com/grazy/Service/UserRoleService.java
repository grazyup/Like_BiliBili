package com.grazy.Service;

import com.grazy.auth.UserRole;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/6 20:40
 * @Description: 用户角色服务层
 */

public interface UserRoleService {

    List<UserRole> getUserRoleDate(Long currentUserId);
}
