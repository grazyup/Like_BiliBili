package com.grazy.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/2 14:20
 * @Description: 关注分组表
 */

@Data
@NoArgsConstructor
public class FollowingGroup {

    private Long id;

    private Long userId;

    private String name;

    /**
     * 关注分组类型：0：特别关注，1：悄悄关注，2：默认关注，3：自定义关注
     */
    private Long type;

    private Date createTime;

    private Date updateTime;

    private List<UserInfo> followingUserInfoList;

}
