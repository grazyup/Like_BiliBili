package com.grazy.mapper;

import com.grazy.domain.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/2 14:23
 * @Description:
 */

@Mapper
public interface FollowingGroupMapper{

    FollowingGroup selectGroupById(Long id);

    List<FollowingGroup> selectGroupByUserId(Long currentUserId);

    FollowingGroup selectGroupByType(@Param("groupType") Long type);

    Long insertFollowingGroup(FollowingGroup followingGroup);
}
