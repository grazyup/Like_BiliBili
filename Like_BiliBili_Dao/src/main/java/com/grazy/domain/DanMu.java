package com.grazy.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/20 15:31
 * @Description: 弹幕类
 */

@Data
public class DanMu {

    private Long id;

    private Long userId;

    private Long videoId;

    private String content;

    /**
     * 弹幕出现时间
     */
    private String danMuTime;

    private Date createTime;
}