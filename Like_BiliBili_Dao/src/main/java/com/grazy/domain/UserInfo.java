package com.grazy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/8/29 20:29
 * @Description:  用户基本信息表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private Long id;

    private Long userId;

    /**
     * 昵称
     */
    private String nick;

    private String avatar;

    private String sign;

    /**
     * 0 男，1 女，2 未知
     */
    private String gender;

    private String birth;

    private Date createTime;

    private Date updateTime;

    /**
     * 是否相互关注
     */
    private Boolean followed;
}
