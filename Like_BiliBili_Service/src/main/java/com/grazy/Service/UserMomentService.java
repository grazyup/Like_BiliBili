package com.grazy.Service;

import com.grazy.domain.UserMoment;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/5 15:19
 * @Description: 用户动态
 */

public interface UserMomentService {

    /**
     * 发布用户动态
     * @param userMoment 动态信息参数
     */
    void addUserMoments(UserMoment userMoment) throws Exception;


    /**
     * 获取个人账户订阅的动态信息
     * @param currentUserId 用户id
     * @return 订阅信息列表
     */
    List<UserMoment> getUserSubscribeMoment(Long currentUserId);
}
