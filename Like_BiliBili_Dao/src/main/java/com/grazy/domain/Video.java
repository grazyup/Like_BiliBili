package com.grazy.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/13 0:31
 * @Description: 投稿视频
 */

@Data
public class Video {

    private Long Id;

    private Long UserId;

    //视频链接地址
    private String url;

    //封面
    private String thumbnail;

    //标题
    private String title;

    // 0自制 1转载
    private String type;

    //时长
    private String duration;

    //分区 0 鬼畜 1 音乐 2 电影
    private String area;

    //视频标签关联列表
    private List<VideoTag> videoTagList;

    //简介
    private String description;

    private Date createTime;

    private Date updateTime;

}
