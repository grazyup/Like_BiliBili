package com.grazy.mapper;

import com.grazy.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: grazy
 * @Date: 2023/9/7 0:37
 * @Description:
 */

@Mapper
public interface AuthRoleElementOperationMapper {

    List<AuthRoleElementOperation> selectAuthElementOperationByRoleIds(@Param("roleIds") Set<Long> roleIds);

}
