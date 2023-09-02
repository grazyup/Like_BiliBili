package com.grazy.Service;

import com.grazy.domain.UserFollowing;

/**
 * @Author: grazy
 * @Date: 2023/9/2 14:27
 * @Description:
 */
public interface UserFollowingService {

    /**
     * 添加关注
     */
    void addFollowing(UserFollowing userFollowing);


    /**
     * 取消关注
     */
    void unfollow(UserFollowing userFollowing);
}
