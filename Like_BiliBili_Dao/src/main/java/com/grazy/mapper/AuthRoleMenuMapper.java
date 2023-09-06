package com.grazy.mapper;

import com.grazy.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: grazy
 * @Date: 2023/9/7 1:04
 * @Description:
 */

@Mapper
public interface AuthRoleMenuMapper {

    List<AuthRoleMenu> selectAuthRoleMenusByRoleIds(@Param("roleIds") Set<Long> roleIds);

}
