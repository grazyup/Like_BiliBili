package com.grazy.mapper;

import com.grazy.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/2 14:23
 * @Description:
 */

@Mapper
public interface UserFollowingMapper {

    void insertFollower(UserFollowing userFollowing);

    UserFollowing selectFollowerById(Long followingId);

    void deleteFollower(Long followingId);

    List<UserFollowing> selectFollowersById(Long currentUserId);

    List<UserFollowing> selectFansByCurrentUserId(Long currentUserId);
}
