package com.grazy.domain;

import lombok.Data;

import java.util.Date;

/**
 * 用户的偏好表
 */
@Data
public class UserPreference {

    private Long id;

    private Long userId;

    private Long videoId;

    /**
     * 用户的操作对应的分值
     */
    private Float value;

    private Date createTime;
}