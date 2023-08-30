package com.grazy.Service;

import com.grazy.domain.User;

/**
 * @Author: grazy
 * @Date: 2023/8/29 20:36
 * @Description:
 */

public interface userService {

    /**
     * 注册用户
     * @param user 用户对象
     */
    void sigIn(User user);


    /**
     * 根据电话查询用户
     * @param phone 电话号码
     * @return 用户对象
     */
    User getUserByPhone(String phone);


    /**
     * 登录
     * @param user 登录对象
     */
    String login(User user);
}
