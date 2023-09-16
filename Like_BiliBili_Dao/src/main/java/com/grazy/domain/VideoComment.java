
package com.grazy.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: grazy
 * @time: 2023/9/16 22:24
 * @description: 视频评论
 */

@Data
public class VideoComment {

    private Long id;

    private Long videoId;

    //创建该评论的用户
    private Long userId;

    //评论
    private String comment;

    //回复用户id
    private Long replyUserId;

    //根节点评论id
    private Long rootId;

    private Date createTime;

    private Date updateTime;

    //当前评论下的二级评论数据列表
    private List<VideoComment> childList;

    //当前用户基本信息
    private UserInfo userInfo;

    //被回复评论的用户的基本信息（上一级评论发布者的基本信息）
    private UserInfo replyUserInfo;
}