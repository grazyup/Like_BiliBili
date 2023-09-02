package com.grazy.Service;

import com.grazy.domain.FollowingGroup;

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
    FollowingGroup selectGroupByType(String type);


    /**
     * 根据分组ID查询分组信息
     * @param Id 分组id
     * @return 分组信息
     */
    FollowingGroup selectGroupById(Long Id);
}
