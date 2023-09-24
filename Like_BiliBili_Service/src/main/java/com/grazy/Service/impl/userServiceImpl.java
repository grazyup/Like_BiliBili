package com.grazy.Service.impl;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.grazy.Common.UserConstant;
import com.grazy.Exception.CustomException;
import com.grazy.Service.ElasticsearchService;
import com.grazy.Service.UserRoleService;
import com.grazy.Service.UserService;
import com.grazy.auth.RefreshTokenDetail;
import com.grazy.domain.PageResult;
import com.grazy.domain.ResultResponse;
import com.grazy.domain.User;
import com.grazy.domain.UserInfo;
import com.grazy.mapper.UserMapper;
import com.grazy.utils.MD5Util;
import com.grazy.utils.RSAUtil;
import com.grazy.utils.TokenUtil;
import com.mysql.cj.util.StringUtils;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author: grazy
 * @Date: 2023/8/29 20:36
 * @Description:
 */

@Service
public class userServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ElasticsearchService elasticsearchService;


    @Override
    @Transactional
    public void sigIn(User user) {
        User dbUser = null;
        //判断用户是使用手机号注册还是邮箱注册
        if(!StringUtils.isNullOrEmpty(user.getPhone())){
            dbUser = getUserByPhone(user.getPhone());
        }else if(!StringUtils.isNullOrEmpty(user.getEmail())){
            dbUser = getUserByPhone(user.getEmail());
        }else{
            throw new CustomException("账号不能为空！");
        }
        //判断该号码是否被注册
        if(dbUser != null){
            throw new CustomException("该用户已被注册！");
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
        //添加用户基本信息到es中
        elasticsearchService.addUserInfo(userInfo);
        //添加用户默认等级角色 --> 在用户-角色关联表中添加新的一条关联数据，将新注册的账号和角色等级关联
        userRoleService.addUserRoleData(user.getId());
    }


    @Override
    public String login(User user) throws Exception{
        User dbUser = null;
        //判断用户是使用电话号码还是邮箱登录
        if(!StringUtils.isNullOrEmpty(user.getPhone())){
            dbUser = getUserByPhone(user.getPhone());
        }else if(!StringUtils.isNullOrEmpty(user.getEmail())){
            dbUser = getUserByEmail(user.getEmail());
        }else{
            throw new CustomException("账号不能为空!");
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
        return TokenUtil.generateAccessToken(dbUser.getId());
    }


    @Override
    public Map<String, Object> loginDoubleTokens(User user) throws Exception {
        User dbUser = null;
        //判断用户是使用电话号码还是邮箱登录
        if(!StringUtils.isNullOrEmpty(user.getPhone())){
            dbUser = getUserByPhone(user.getPhone());
        }else if(!StringUtils.isNullOrEmpty(user.getEmail())){
            dbUser = getUserByEmail(user.getEmail());
        }else{
            throw new CustomException("账号不能为空!");
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
        //获取访问token
        String accessToken = TokenUtil.generateAccessToken(dbUser.getId());
        //获取刷新token
        String refreshToken = TokenUtil.generateRefreshToken(dbUser.getId());
        //删除原先的刷新token，再新的token添加到数据库
        userMapper.deleteRefreshToken(refreshToken,dbUser.getId());
        userMapper.insertRefreshToken(refreshToken,dbUser.getId(),new Date());
        //封装到map集合中
        Map<String,Object> doubleTokens = new HashMap<>();
        doubleTokens.put("accessToken",accessToken);
        doubleTokens.put("refreshToken",refreshToken);
        return doubleTokens;
    }


    @Override
    public void logout(String refreshToken, Long currentId) {
        //删除退出登录账号的刷新token
        userMapper.deleteRefreshToken(refreshToken,currentId);
    }


    @Override
    public String getNewAccessTokenByRefreshToken(String refreshToken) throws Exception {
        //根据传入参数获取刷新token数据
        RefreshTokenDetail refreshTokenDetail = userMapper.selectRefreshToken(refreshToken);
        String accessToken = null;
        if(refreshTokenDetail == null){
            throw new CustomException("556","RefreshToken已过期!");
        }else{
            try {
                //检验RefreshToken是否过期
                if(TokenUtil.verifyRefreshToken(refreshToken)){
                    //获取用户id,生成新的token返回
                    accessToken = TokenUtil.generateRefreshToken(refreshTokenDetail.getUserId());
                }
            }catch (TokenExpiredException e){
                //删除存在数据库的refreshToken数据
                userMapper.deleteRefreshToken(refreshTokenDetail.getRefreshToken(), refreshTokenDetail.getUserId());
                throw new CustomException("556","RefreshToken已过期!");
            } catch (Exception e){
                throw new CustomException("非法Token！");
            }
        }
        return accessToken;
    }


    @Override
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }


    @Override
    public User getUserByPhone(String phone) {
        return userMapper.getUserByPhone(phone);
    }


    @Override
    public User getUserDateById(Long currentUserId) {
        //获取用户账号数据
        User user = userMapper.selectUserById(currentUserId);
        //获取用户基本信息
        List<UserInfo> userInfoList = userMapper.selectUserInfoById(new HashSet<Long>(){{add(currentUserId);}});
        user.setUserInfo(userInfoList.get(0));
        return user;
    }


    @Override
    public void updateUserInfo(UserInfo userInfo, Long userId){
        //设置更改时间
        userInfo.setUpdateTime(new Date());
        if(userMapper.updateUserInfoById(userId, userInfo) == 0){
            throw new CustomException("用户基本信息修改失败！");
        }
    }


    @Override
    public void updateUser(User user, Long currentUserId) throws Exception {
        user.setUpdateTime(new Date());
        User dbUser = userMapper.selectUserById(currentUserId);
        if(dbUser == null) throw new CustomException("用户不存在！");
        if(!StringUtils.isNullOrEmpty(user.getPassword())){
            //当前涉及修改密码
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String MdPassword = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
            user.setPassword(MdPassword);
        }
        if(userMapper.updateUserById(currentUserId, user) == 0){
            throw new CustomException("用户账号信息修改失败！");
        }
    }



    @Override
    public List<UserInfo> selectUserInfoBy(Set<Long> ids) {
        return userMapper.selectUserInfoById(ids);
    }


    @Override
    public PageResult<UserInfo> pageListUserInfo(JSONObject params) {
        //设置查询起始位置
        params.put("startNumber",(params.getInteger("current")-1) * params.getInteger("size"));
        params.put("limit",params.getInteger("size"));
        Integer total = userMapper.pageCountUserInfo(params);
        List<UserInfo> records = new ArrayList<>();
        if(total > 0){
            records = userMapper.pageListUserInfo(params);
        }
        return new PageResult<>(total,records);
    }


}
