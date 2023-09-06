package com.grazy.Service;

import com.grazy.auth.UserAuthorities;

/**
 * @Author: grazy
 * @Date: 2023/9/6 20:36
 * @Description: 用户权限服务层
 */
public interface UserAuthoritiesService {


    /**
     * 获取账号权限
     * @param currentUserId 用户id
     * @return 权限信息
     */
    UserAuthorities getUserAuthorities(Long currentUserId);
}
