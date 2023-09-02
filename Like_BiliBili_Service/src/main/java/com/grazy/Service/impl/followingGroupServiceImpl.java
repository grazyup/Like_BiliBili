package com.grazy.Service.impl;

import com.grazy.Service.FollowingGroupService;
import com.grazy.domain.FollowingGroup;
import com.grazy.mapper.FollowingGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: grazy
 * @Date: 2023/9/2 14:26
 * @Description:
 */

@Service
public class followingGroupServiceImpl implements FollowingGroupService {

    @Autowired
    private FollowingGroupMapper followingGroupMapper;

    @Override
    public FollowingGroup selectGroupByType(String type) {
        return null;
    }

    @Override
    public FollowingGroup selectGroupById(Long Id) {
        return null;
    }
}
