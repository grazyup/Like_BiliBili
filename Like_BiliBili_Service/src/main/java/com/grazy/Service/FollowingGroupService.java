package com.grazy.Service;

import com.grazy.domain.FollowingGroup;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/2 14:26
 * @Description:
 */

public interface FollowingGroupService {

    /**
     * 根据分组类型获取分组
     * @param type 分组类型
     * @return 分组信息
     */
    FollowingGroup selectGroupByType(Long type);


    /**
     * 根据分组ID查询分组信息
     * @param Id 分组id
     * @return 分组信息
     */
    FollowingGroup selectGroupById(Long Id);


    /**
     * 获取用户关注分组数据列表
     * @param currentUserId 当前用户id
     * @return 分组集合
     */
    List<FollowingGroup> selectGroupByUserId(Long currentUserId);


    /**
     * 新增关注分组
     * @param followingGroup 新的分组对象信息
     * @return 分组id
     */
    Long insertFollowingGroup(FollowingGroup followingGroup);
}
