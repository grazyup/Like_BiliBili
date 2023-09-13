package com.grazy.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/13 0:35
 * @Description: 视频标签关联表类
 */

@Data
public class VideoTag {

    private Long id;

    private Long videoId;

    private Long tagId;

    private Date createTime;

}
