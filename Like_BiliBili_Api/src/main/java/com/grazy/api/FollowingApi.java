package com.grazy.api;

import com.grazy.Service.UserFollowingService;
import com.grazy.domain.ResultResponse;
import com.grazy.domain.UserFollowing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: grazy
 * @Date: 2023/9/2 15:48
 * @Description: 关注和关注分组
 */

@RestController
public class FollowingApi {

    @Autowired
    private UserFollowingService userFollowingService;


    /**
     * 关注
     * @param userFollowing 用户关注参数
     * @return 响应结果
     */
    @PostMapping("/following")
    public ResultResponse<String> following(@RequestBody UserFollowing userFollowing){
        userFollowingService.addFollowing(userFollowing);
        return ResultResponse.success("关注成功！");
    }


    @PostMapping("/unfollow")
    public ResultResponse<String> unfollow(@RequestBody UserFollowing userFollowing){
        userFollowingService.unfollow(userFollowing);
        return ResultResponse.success("取消关注成功！");
    }
}
