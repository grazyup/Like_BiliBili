package com.grazy.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/2 14:18
 * @Description:  用户关注表
 */

@Data
@NoArgsConstructor
public class UserFollowing {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 关注用户id
     */
    private Long followingId;

    /**
     * 关注分组id
     */
    private Long groupId;

    private Date createTime;

    private UserInfo userInfo;

}