package com.grazy.mapper;

import com.grazy.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: grazy
 * @Date: 2023/9/5 15:20
 * @Description:
 */

@Mapper
public interface UserMomentMapper {

    void insertMoment(UserMoment userMoment);
}
