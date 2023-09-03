package com.grazy.Service.impl;

import com.grazy.Common.UserConstant;
import com.grazy.Exception.CustomException;
import com.grazy.Service.FollowingGroupService;
import com.grazy.Service.UserFollowingService;
import com.grazy.Service.UserService;
import com.grazy.domain.FollowingGroup;
import com.grazy.domain.UserFollowing;
import com.grazy.domain.UserInfo;
import com.grazy.mapper.UserFollowingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            FollowingGroup followingGroup = followingGroupService.selectGroupByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFALUT);
            //未指定分组，添加到默认分组
            userFollowing.setGroupId(followingGroup.getId());
        }else{
            //判断指定的分组是否存在
            if(followingGroupService.selectGroupById(groupId) == null){
                throw new CustomException("该分组不存在!");
            }
        }
        //判断关注的对象账号是否存在
        if(userService.getUserDateById(userFollowing.getFollowingId()) == null){
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


    @Override
    public List<FollowingGroup> getFollowersList(Long currentUserId) {
        //获取该用户账号下的分组对象
        List<FollowingGroup> followingGroupList = followingGroupService.selectGroupByUserId(currentUserId);
        //获取关注表的关注信息数据列表
        List<UserFollowing> followersData = userFollowingMapper.selectFollowersById(currentUserId);
        //提取全部关注者id
        Set<Long> followingIds = followersData.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        if(followingIds.size() == 0){
            throw new CustomException("未有关注者！");
        }
        //获取关注者的个人信息
        List<UserInfo> followersInfoList = userService.selectUserInfoBy(followingIds);
        //将用户信息封装到对应的UserFollowing中去
        for(UserFollowing el: followersData){
            for(UserInfo followersInfo: followersInfoList){
                if(followersInfo.getUserId().equals(el.getFollowingId())){
                    el.setUserInfo(followersInfo);
                }
            }
        }
        //创建全部关注的分组
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(followersInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        //将关注者信息封装到对应的分组中
        for(FollowingGroup followingGroup: followingGroupList) {
            List<UserInfo> list = new ArrayList<>();
            for (UserFollowing userFollowing : followersData) {
                if (followingGroup.getId().equals(userFollowing.getGroupId())) {
                    list.add(userFollowing.getUserInfo());
                }
            }
            followingGroup.setFollowingUserInfoList(list);
            result.add(followingGroup);
        }
        return result;
    }


    @Override
    public List<UserFollowing> getFansInfo(Long currentUserId) {
        //查询关注表中 followingId 为 currentUserId 的 userId 集合
        List<UserFollowing> fansList = userFollowingMapper.selectFansByCurrentUserId(currentUserId);
        Set<Long> fansIds = fansList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        if(fansIds.size() == 0){
            throw new CustomException("暂时没有粉丝关注你！");
        }
        //获取当前登录用户的关注对象集合
        List<UserFollowing> userFollowerList = userFollowingMapper.selectFollowersById(currentUserId);
        //根据userId集合查询集合的用户信息
        List<UserInfo> fansInfoList = userService.selectUserInfoBy(fansIds);
        for(UserFollowing fans: fansList){
            for(UserInfo fansInfo: fansInfoList){
                if(fans.getUserId().equals(fansInfo.getUserId())){
                    //先全部设置与当前用户为未互相关注
                    fansInfo.setFollowed(false);
                    fans.setUserInfo(fansInfo);
                }
            }
            //判断是否互相关注
            for(UserFollowing userFollower: userFollowerList){
                if(userFollower.getFollowingId().equals(fans.getUserId())){
                    //设置为互相关注
                    fans.getUserInfo().setFollowed(true);
                }
            }
        }
        return fansList;
    }


    @Override
    public List<UserInfo> checkFollowingStatus(List<UserInfo> records, Long currentUserId) {
        //查询当前用户的关注信息
        List<UserFollowing> userFollowings = userFollowingMapper.selectFollowersById(currentUserId);
        for(UserInfo userInfo: records){
            //将查询的用户关注属性全部设置为false
            userInfo.setFollowed(false);
            for(UserFollowing userFollowing: userFollowings){
                if(userInfo.getUserId().equals(userFollowing.getFollowingId())){
                    //找到的用户与当前用户存在相互关注
                    userInfo.setFollowed(true);
                }
            }
        }
        return records;
    }
}
