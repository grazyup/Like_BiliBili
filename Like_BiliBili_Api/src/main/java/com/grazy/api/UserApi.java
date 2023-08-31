package com.grazy.api;

import com.grazy.Service.userService;
import com.grazy.domain.ResultResponse;
import com.grazy.domain.User;
import com.grazy.support.userSupport;
import com.grazy.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: grazy
 * @Date: 2023/8/29 20:38
 * @Description:
 */

@RestController
public class UserApi {

    @Autowired
    private userService userService;

    @Autowired
    private userSupport userSupport;


    /**
     * 获取RSA加密公钥
     */
    @GetMapping("/rsa-pks")
    public ResultResponse<String> getPublicKey(){
        return ResultResponse.success("获取成功", RSAUtil.getPublicKeyStr());
    }


    /**
     * 注册
     * @return
     */
    @PostMapping("/users")
    public ResultResponse<String> SignIn(@RequestBody User user){
        userService.sigIn(user);
        return ResultResponse.success("注册成功");
    }


    /**
     * 登录
     */
    @GetMapping("/users-token")
    public ResultResponse<String> login(@RequestBody User user) throws Exception{
        return ResultResponse.success("登录成功",userService.login(user));
    }


    /**
     * 根据id获取用户信息
     * @return 用户信息
     */
    @GetMapping("/users")
    public ResultResponse<User> getUserInfo(){
        //通过token获取用户参数
        Long currentUserId = userSupport.getCurrentUserId();
        User user = userService.getUserInfoById(currentUserId);
        return ResultResponse.success("获取成功", user);
    }
}
