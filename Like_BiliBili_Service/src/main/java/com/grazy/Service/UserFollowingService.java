package com.grazy.Service;

import com.grazy.domain.FollowingGroup;
import com.grazy.domain.UserFollowing;

import java.util.List;

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


    /**
     * 获取关注列表
     * @param currentUserId 登录的用户id
     * @return 关注列表
     */
    List<FollowingGroup> getFollowersList(Long currentUserId);


    /**
     * 获取粉丝信息
     * @return 粉丝信息列表
     */
    List<UserFollowing> getFansInfo(Long currentUserId);
}
