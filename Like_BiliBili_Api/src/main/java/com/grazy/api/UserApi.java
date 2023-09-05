package com.grazy.api;

import com.alibaba.fastjson.JSONObject;
import com.grazy.Service.UserFollowingService;
import com.grazy.Service.UserService;
import com.grazy.domain.*;
import com.grazy.support.UserSupport;
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
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollowingService;


    /**
     * 获取RSA加密公钥
     */
    @GetMapping("/rsa-pks")
    public ResultResponse<String> getPublicKey(){
        String publicKeyStr = RSAUtil.getPublicKeyStr();
        return ResultResponse.success("公钥获取成功",publicKeyStr);
    }


    /**
     * 注册
     * @return 响应数据
     */
    @PostMapping("/users")
    public ResultResponse<String> SignIn(@RequestBody User user){
        userService.sigIn(user);
        return ResultResponse.success("注册成功");
    }


    /**
     * 登录
     * @param user 账号密码存储用户对象
     * @return token
     * @throws Exception 异常
     */
    @GetMapping("/user-tokens")
    public ResultResponse<String> login(@RequestBody User user) throws Exception{
        return ResultResponse.success("token登录成功",userService.login(user));
    }


    /**
     * 根据id获取用户信息
     * @return 用户信息
     */
    @GetMapping("/users")
    public ResultResponse<User> getUserInfo(){
        //通过token获取用户参数
        Long currentUserId = userSupport.getCurrentUserId();
        User user = userService.getUserDateById(currentUserId);
        return ResultResponse.success("获取成功", user);
    }


    /**
     * 更新用户基本信息
     * @param userInfo 更新后的基本信息对象
     * @return 响应数据
     */
    @PutMapping("/user-infos")
    public ResultResponse<String> updateUserInfos(@RequestBody UserInfo userInfo){
        //通过token解析获取userId,避免被仿照id调用接口
        userService.updateUserInfo(userInfo,userSupport.getCurrentUserId());
        return ResultResponse.success("信息修改成功！");
    }


    /**
     * 更新用户账户信息
     * @param user 携带新数据的用户对象
     * @return 响应结果
     */
    @PutMapping("/users")
    public ResultResponse<String> updateUser(@RequestBody User user) throws Exception{
        //token解析出userid
        Long currentUserId = userSupport.getCurrentUserId();
        userService.updateUser(user,currentUserId);
        return ResultResponse.success("更新成功！");
    }


    /**
     * 分页查询用户信息
     * @param current 当前页码
     * @param size 每页展示的条数
     * @param conditionNick 昵称查询条件
     * @return 用户信息集合
     */
    @GetMapping("/page-users")
    public ResultResponse<PageResult<UserInfo>> pageListUserInfo(@RequestParam(defaultValue = "1") Integer current,
                                                                 @RequestParam(defaultValue = "10") Integer size,
                                                                 String conditionNick){
        //fastJson依赖提供实现map的一个类
        JSONObject params = new JSONObject();
        Long currentUserId = userSupport.getCurrentUserId();
        params.put("current",current);
        params.put("size",size);
        params.put("condition",conditionNick);
        params.put("userId",currentUserId);
        PageResult<UserInfo> result = userService.pageListUserInfo(params);
        //判断搜索出来的用户与当前账户是否互相关注
        if (result.getTotal() > 0) {
            result.setRecords(userFollowingService.checkFollowingStatus(result.getRecords(),currentUserId));
        }
        return ResultResponse.success("查询成功！",result);
    }


}
