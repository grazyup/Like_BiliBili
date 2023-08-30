package com.grazy.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/8/29 20:28
 * @Description: 用户表
 */

@Data
public class User {

    private Long id;

    private String phone;

    private String email;

    private String password;

    /**
     * 盐值
     */
    private String salt;

    private Date createTime;

    private Date updateTime;

    /**
     * 用户信息表id
     */
    private UserInfo userInfo;
}
