package com.grazy.mapper;

import com.grazy.domain.User;
import com.grazy.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

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

    List<UserInfo> selectUserInfoById(Set<Long> Ids);

    Integer updateUserInfoById(@Param("userId") Long userId, @Param("userInfo") UserInfo userInfo);

    Integer updateUserById(@Param("userId") Long currentUserId, @Param("user") User user);

    User getUserByEmail(String email);
}
