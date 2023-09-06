package com.grazy.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: grazy
 * @time: 2023/9/16
 * @description: 用户-角色中间表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    private Long id;

    private Long userId;

    private Long roleId;

    private Date createTime;

    private String roleName;

    private String roleCode;

}
