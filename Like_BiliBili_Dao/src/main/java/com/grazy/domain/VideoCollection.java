
package com.grazy.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/16 13:46
 * @Description: 视频收藏记录表
 */

@Data
public class VideoCollection {

    private Long id;

    private Long videoId;

    private Long userId;

    private Long groupId;

    private Date createTime;

}