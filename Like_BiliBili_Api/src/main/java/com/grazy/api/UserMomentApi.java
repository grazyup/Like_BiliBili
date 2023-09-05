package com.grazy.api;

import com.grazy.Service.UserMomentService;
import com.grazy.domain.ResultResponse;
import com.grazy.domain.UserMoment;
import com.grazy.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/5 15:18
 * @Description:  用户动态api
 */

@RestController
public class UserMomentApi {

    @Autowired
    private UserMomentService userMomentService;

    @Autowired
    private UserSupport userSupport;


    /**
     * 发布用户动态
     * @param userMoment 用户动态信息参数
     * @return 响应结果
     */
    @PostMapping("/users-moments")
    public ResultResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception{
        userMoment.setUserId(userSupport.getCurrentUserId());
        userMomentService.addUserMoments(userMoment);
        return ResultResponse.success("发布成功！");
    }


    /**
     * 获取个人账户订阅的动态信息
     * @return 订阅动态信息
     */
    @GetMapping("/users-subscribed-moments")
    public ResultResponse<List<UserMoment>> getUserSubscribedMoment(){
        List<UserMoment> userMomentList = userMomentService.getUserSubscribeMoment(userSupport.getCurrentUserId());
        return ResultResponse.success("获取成功！",userMomentList);
    }


}
