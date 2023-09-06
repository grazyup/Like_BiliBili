package com.grazy.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: grazy
 * @time: 2023/9/16
 * @description: 菜单
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthMenu {

    private Long id;

    /**
     * 菜单项目名称
     */
    private String name;

    /**
     * 唯一编码
     */
    private String code;

    private Date createTime;

    private Date updateTime;
}
