package com.grazy.Service.impl;

import com.grazy.Common.UserConstant;
import com.grazy.Exception.CustomException;
import com.grazy.Service.userService;
import com.grazy.domain.User;
import com.grazy.domain.UserInfo;
import com.grazy.mapper.UserMapper;
import com.grazy.utils.MD5Util;
import com.grazy.utils.RSAUtil;
import com.grazy.utils.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/8/29 20:36
 * @Description:
 */

@Service
public class userServiceImpl implements userService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public void sigIn(User user) {
        //获取注册的手机号
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)){
            //电话号码为空，抛异常
            throw new CustomException("电话号码为空！");
        }
        //判断该号码是否被注册
        if(getUserByPhone(phone) != null){
            throw new CustomException("此号码已被注册！");
        }
        String rawPassword;  //原始密码
        try {
            //对传输的RSA加密的密码进行解密
            rawPassword = RSAUtil.decrypt(user.getPassword());
        } catch (Exception e) {
            throw new CustomException("密码解密失败！");
        }
        //对密码进行MD5加密并存入数据库
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String MD5password = MD5Util.sign(rawPassword, salt, "UTF-8");
        //将数据填写入用户表中
        user.setPassword(MD5password);
        user.setCreateTime(now);
        user.setSalt(salt);
        userMapper.addUser(user);
        //创建用户基本信息表
        UserInfo userInfo = new UserInfo(null,user.getId(), UserConstant.DEFAULT_NICK,null,
                null,UserConstant.GENDER_MALE, UserConstant.DEFAULT_BIRTH,now,null,null);
        //数据添加到用户基本信息表中
        userMapper.addUserInfo(userInfo);
    }


    @Override
    public String login(User user) throws Exception{
        //数据库存储的用户
        User dbUser = getUserByPhone(user.getPhone());
        if(StringUtils.isNullOrEmpty(user.getPhone())){
            //手机号码为空
            throw new CustomException("手机号码为空");
        }
        if(dbUser == null){
            //未注册
            throw new CustomException("该账号未注册!");
        }
        //原始密码
        String rawPassword = null;
        try {
            //RSA解密
            rawPassword = RSAUtil.decrypt(user.getPassword());
        } catch (Exception e) {
            throw new CustomException("密码RSA解密失败！");
        }
        //将原始密码进行MD5加密
        String MDPassword = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
        //判断数据库存储的密码与传入的密码是否正确
        if(!dbUser.getPassword().equals(MDPassword)){
            throw new CustomException("密码错误！");
        }
        return TokenUtil.generateToken(dbUser.getId());
    }


    @Override
    public User getUserInfoById(Long currentUserId) {
        //获取用户账号数据
        User user = userMapper.selectUserById(currentUserId);
        //获取用户基本信息
        UserInfo userInfo = userMapper.selectUserInfoById(currentUserId);
        user.setUserInfo(userInfo);
        return user;
    }


    @Override
    public User getUserByPhone(String phone) {
        return userMapper.getUserByPhone(phone);
    }


}
