package com.grazy.api;

import com.grazy.Service.UserAuthoritiesService;
import com.grazy.auth.UserAuthorities;
import com.grazy.domain.ResultResponse;
import com.grazy.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: grazy
 * @Date: 2023/9/6 20:30
 * @Description: 用户权限api
 */

@RestController
public class UserAuthApi {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserAuthoritiesService userAuthoritiesService;


    /**
     * 获取用户的权限信息
     * @return 权限信息
     */
    @GetMapping("/user-authorities")
    public ResultResponse<UserAuthorities>getUserAuthorities(){
        return ResultResponse.success("获取成功!",
                userAuthoritiesService.getUserAuthorities(userSupport.getCurrentUserId()));
    }


}
