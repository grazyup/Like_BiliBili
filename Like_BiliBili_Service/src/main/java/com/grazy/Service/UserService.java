package com.grazy.Service;

import com.alibaba.fastjson.JSONObject;
import com.grazy.domain.PageResult;
import com.grazy.domain.User;
import com.grazy.domain.UserInfo;

import java.util.List;
import java.util.Set;

/**
 * @Author: grazy
 * @Date: 2023/8/29 20:36
 * @Description:
 */

public interface UserService {

    /**
     * 注册用户
     * @param user 用户对象
     */
    void sigIn(User user);


    /**
     * 根据电话查询用户
     * @param phone 电话号码
     * @return 用户对象
     */
    User getUserByPhone(String phone);


    /**
     * 登录
     * @param user 登录对象
     * @return 返回生成的token
     */
    String login(User user) throws Exception;


    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象
     */
    User getUserByEmail(String email);


    /**
     * 根据id获取用户信息
     * @param currentUserId 当前用户id
     * @return 用户和用户基本信息表数据
     */
    User getUserDateById(Long currentUserId);


    /**
     * 修改用户基本信息
     * @param userInfo 更新后的信息对象
     */
    void updateUserInfo(UserInfo userInfo,Long userId);


    /**
     * 更新用户账户信息
     * @param user 携带新数据的用户对象
     */
    void updateUser(User user, Long currentUserId) throws Exception;


    /**
     * 获取用户info
     * @param ids id
     * @return 用户信息
     */
    List<UserInfo> selectUserInfoBy(Set<Long> ids);


    /**
     * 分页查询用户信息
     * @param params 封装的分页查询参数对象
     * @return 分页用户数据
     */
    PageResult<UserInfo> pageListUserInfo(JSONObject params);
}
