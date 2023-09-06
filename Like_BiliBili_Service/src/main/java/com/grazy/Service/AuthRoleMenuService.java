package com.grazy.Service;

import com.grazy.auth.AuthRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * @Author: grazy
 * @Date: 2023/9/7 0:59
 * @Description: 角色-访问页面权限表服务
 */

public interface AuthRoleMenuService {

    /** 联表查询页面访问权限
     * @param roleIds 角色id
     * @return 封装好权限信息的角色-页面访问中间表类集合
     */
    List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIds);
}
