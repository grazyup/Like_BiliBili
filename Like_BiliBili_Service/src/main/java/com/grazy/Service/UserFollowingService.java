package com.grazy.Service;

import com.grazy.domain.FollowingGroup;
import com.grazy.domain.UserFollowing;
import com.grazy.domain.UserInfo;

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


    /**
     * 判断是否关注了其中的某些用户
     * @param records 用户列表
     * @param currentUserId 当前用户id
     * @return 用户列表
     */
    List<UserInfo> checkFollowingStatus(List<UserInfo> records, Long currentUserId);
}
