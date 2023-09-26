package com.grazy.domain;

import lombok.Data;

import java.util.Date;

@Data
public class VideoBinaryPicture {

    private Long id;

    private Long videoId;

    private Integer frameNo;

    private String url;

    private Long videoTimestamp;

    private Date createTime;
}