package com.grazy.Service;

import com.grazy.auth.AuthRole;
import com.grazy.auth.AuthRoleElementOperation;
import com.grazy.auth.AuthRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * @Author: grazy
 * @Date: 2023/9/6 23:07
 * @Description: 角色表服务层
 */

public interface AuthRoleService {

    /**
     * 获取角色元素操作权限
     * @param roleIds 角色id集合
     */
    List<AuthRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIds);


    /**
     * 获取角色访问页面权限
     * @param roleIds 角色id集合
     */
    List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIds);

    /**
     * 根据角色等级编码获取角色信息
     * @param code 角色等级编码
     * @return 角色数据
     */
    AuthRole getRoleByCode(String code);
}
