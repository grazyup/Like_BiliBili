package com.grazy.api;

import com.grazy.Service.userService;
import com.grazy.domain.ResultResponse;
import com.grazy.domain.User;
import com.grazy.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: grazy
 * @Date: 2023/8/29 20:38
 * @Description:
 */

@RestController
public class UserApi {

    @Autowired
    private userService userService;


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
    @GetMapping("/user-token")
    public ResultResponse<String> login(@RequestBody User user){
        return ResultResponse.success("登录成功",userService.login(user));
    }

}
