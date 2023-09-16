package com.grazy.Service;

/**
 * @Author: grazy
 * @Date: 2023/9/16 17:00
 * @Description: 用户硬币业务层
 */

public interface UserCoinsService {

    /**
     * 获取用户账户的硬币数量
     * @param currentUserId 用户id
     * @return 硬币数量
     */
    Integer getUserCoinsAmount(Long currentUserId);


    /**
     * 更新用户账号的硬币数量
     * @param currentUserId 用户id
     * @param newCoinsAmount 新的硬币数量
     */
    void updateUserCoinsAmount(Long currentUserId, int newCoinsAmount);
}
