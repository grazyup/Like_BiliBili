package com.grazy.Service.impl;

import com.grazy.Service.UserCoinsService;
import com.grazy.mapper.UserCoinsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/16 17:01
 * @Description:
 */

@Service
public class UserCoinsServiceImpl implements UserCoinsService {

    @Autowired
    private UserCoinsMapper userCoinsMapper;

    @Override
    public Integer getUserCoinsAmount(Long currentUserId) {
        return userCoinsMapper.selectUserCoinsAmount(currentUserId);
    }

    @Override
    public void updateUserCoinsAmount(Long currentUserId, int newCoinsAmount) {
        userCoinsMapper.updateUserCoinsAmount(currentUserId,newCoinsAmount,new Date());
    }
}
