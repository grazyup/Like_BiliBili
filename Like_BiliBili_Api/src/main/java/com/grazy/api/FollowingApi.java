package com.grazy.api;

import com.grazy.Service.FollowingGroupService;
import com.grazy.Service.UserFollowingService;
import com.grazy.domain.*;
import com.grazy.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/2 15:48
 * @Description: 关注 和 关注分组
 */

@RestController
public class FollowingApi {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport support;

    @Autowired
    private FollowingGroupService followingGroupService;


    /**
     * 关注
     * @param userFollowing 用户关注参数
     * @return 响应结果
     */
    @PostMapping("/following")
    public ResultResponse<String> following(@RequestBody UserFollowing userFollowing){
        //解析token获取userid
        userFollowing.setUserId(support.getCurrentUserId());
        userFollowingService.addFollowing(userFollowing);
        return ResultResponse.success("关注成功！");
    }


    /**
     * 取关
     * @param userFollowing 用户关注参数
     * @return 响应结果
     */
    @PostMapping("/unfollow")
    public ResultResponse<String> unfollow(@RequestBody UserFollowing userFollowing){
        userFollowing.setUserId(support.getCurrentUserId());
        userFollowingService.unfollow(userFollowing);
        return ResultResponse.success("取消关注成功！");
    }


    /**
     * 获取用户关注列表
     * @return 关注列表
     */
    @GetMapping("/get-followers")
    public ResultResponse<List<FollowingGroup>> getFollowersList(){
        //解析token获取userid
        List<FollowingGroup> followersList = userFollowingService.getFollowersList(support.getCurrentUserId());
        return ResultResponse.success("获取成功",followersList);
    }


    /**
     * 获取粉丝信息
     * @return 粉丝信息列表
     */
    @GetMapping("/get-fansInfo")
    public ResultResponse<List<UserFollowing>> getFansInfo(){
        //解析token获取userid
        Long currentUserId = support.getCurrentUserId();
        List<UserFollowing> fansList = userFollowingService.getFansInfo(currentUserId);
        return ResultResponse.success("粉丝信息列表获取成功！",fansList);
    }


    /**
     * 新建关注分组
     * @return 分组id
     */
    @PostMapping("/user-following-groups")
    public ResultResponse<Long> addUserFollowingGroup(@RequestBody FollowingGroup followingGroup){
        followingGroup.setUserId(support.getCurrentUserId());
       return ResultResponse.success("创建成功",followingGroupService.insertFollowingGroup(followingGroup));
    }


    /**
     * 获取当前用户的关注分组
     * @return 关注分组列表
     */
    @GetMapping("/user-following-groups")
    public ResultResponse<List<FollowingGroup>> getFollowingGroup(){
        return ResultResponse.success("分组列表获取成功!",followingGroupService.selectGroupByUserId(support.getCurrentUserId()));
    }
}
