package com.grazy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/16 20:36
 * @Description:
 */

@Mapper
public interface UserCoinsMapper {

    Integer selectUserCoinsAmount(Long currentUserId);

    void updateUserCoinsAmount(@Param("userId") Long currentUserId,
                               @Param("newCoinsAmount") int newCoinsAmount,
                               @Param("updateTime")Date updateTime);

}
