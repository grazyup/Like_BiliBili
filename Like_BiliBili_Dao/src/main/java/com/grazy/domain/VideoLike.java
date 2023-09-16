package com.grazy.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/15 15:22
 * @Description: 视频点赞记录表
 */

@Data
public class VideoLike {

    private Long id;

    private Long userId;

    private Long videoId;

    private Date createTime;

}