package com.grazy.Service.impl;

import com.grazy.Common.UserConstant;
import com.grazy.Service.FollowingGroupService;
import com.grazy.domain.FollowingGroup;
import com.grazy.mapper.FollowingGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    public FollowingGroup selectGroupByType(Long type) {
        return followingGroupMapper.selectGroupByType(type);
    }

    @Override
    public FollowingGroup selectGroupById(Long Id) {
        return followingGroupMapper.selectGroupById(Id);
    }

    @Override
    public List<FollowingGroup> selectGroupByUserId(Long currentUserId) {
        return followingGroupMapper.selectGroupByUserId(currentUserId);
    }

    @Override
    public Long insertFollowingGroup(FollowingGroup followingGroup) {
        followingGroup.setName(followingGroup.getUserId() + "的新建分组" + new Date().getMonth() + new Date().getDate());
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        //插入数据库
        return followingGroupMapper.insertFollowingGroup(followingGroup);
    }
}
