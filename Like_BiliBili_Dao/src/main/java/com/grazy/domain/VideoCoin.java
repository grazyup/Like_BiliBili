package com.grazy.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/16 16:46
 * @Description: 视频投币记录表
 */


@Data
public class VideoCoin {

    private Long id;

    private Long videoId;

    private Long userId;

    private Integer amount;

    private Date createTime;

    private Date updateTime;
}