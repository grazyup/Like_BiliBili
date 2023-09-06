package com.grazy.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: grazy
 * @time: 2023/9/16
 * @description: 角色表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRole {

    private Long id;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色唯一编码
     */
    private String code;
    private Date createTime;
    private Date updateTime;
}
