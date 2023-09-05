package com.grazy.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/5 15:25
 * @Description: 用户动态表
 */

@Data
public class UserMoment {

    private Long id;

    private Long userId;

    /**
     * 动态类型：0 视频、 1 直播、 2 专栏动态
     */
    private String type;

    /**
     * 内容详情id
     */
    private Long contentId;

    private Date createTime;

    private Date updateTime;

}
