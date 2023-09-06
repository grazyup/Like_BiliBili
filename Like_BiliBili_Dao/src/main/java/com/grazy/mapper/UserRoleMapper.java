package com.grazy.mapper;

import com.grazy.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/6 20:45
 * @Description: 用户角色数据层
 */

@Mapper
public interface UserRoleMapper {

    List<UserRole> selectUserRoleDateByUserId(Long currentUserId);
}
