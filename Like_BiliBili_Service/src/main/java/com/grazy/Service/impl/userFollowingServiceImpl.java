package com.grazy.Service.impl;

import com.grazy.Common.UserConstant;
import com.grazy.Exception.CustomException;
import com.grazy.Service.FollowingGroupService;
import com.grazy.Service.UserFollowingService;
import com.grazy.Service.UserService;
import com.grazy.domain.UserFollowing;
import com.grazy.mapper.UserFollowingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/2 14:27
 * @Description:
 */

@Service
public class userFollowingServiceImpl implements UserFollowingService {

    @Autowired
    private UserFollowingMapper userFollowingMapper;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;


    @Override
    public void addFollowing(UserFollowing userFollowing) {
        //获取关注分组的id
        Long groupId = userFollowing.getGroupId();
        if(groupId == null){
            //未指定分组，添加到默认分组
            userFollowing.setGroupId(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFALUT);
        }else{
            //判断指定的分组是否存在
            if(followingGroupService.selectGroupById(groupId) == null){
                throw new CustomException("该分组不存在!");
            }
        }
        //判断关注的对象账号是否存在
        if(userService.getUserInfoById(userFollowing.getFollowingId()) == null){
            throw new CustomException("关注的用户账号不存在!");
        }
        //判断是否已关注该对象
        if(userFollowingMapper.selectFollowerById(userFollowing.getFollowingId()) != null){
            throw new ClassCastException("该用户已关注！");
        }
        userFollowing.setCreateTime(new Date());
        userFollowingMapper.insertFollower(userFollowing);
    }


    @Override
    public void unfollow(UserFollowing userFollowing) {
        //判断是否关注该用户
        UserFollowing followerDate = userFollowingMapper.selectFollowerById(userFollowing.getFollowingId());
        if(followerDate == null){
            throw new CustomException("该用户未关注！");
        }
        //取关
        userFollowingMapper.deleteFollower(userFollowing.getFollowingId());
    }
}
