package com.grazy.mapper;

import com.grazy.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: grazy
 * @Date: 2023/9/6 23:35
 * @Description:
 */

@Mapper
public interface AuthRoleMapper {

    AuthRole selectRoleByCode(String code);
}
