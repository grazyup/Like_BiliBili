package com.grazy.mapper;

import com.grazy.domain.User;
import com.grazy.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: grazy
 * @Date: 2023/8/29 20:41
 * @Description:
 */

@Mapper
public interface UserMapper {

    User getUserByPhone(String phone);

    void addUser(User user);

    void addUserInfo(UserInfo userInfo);

    User selectUserById(Long currentUserId);

    UserInfo selectUserInfoById(Long currentUserId);
}
