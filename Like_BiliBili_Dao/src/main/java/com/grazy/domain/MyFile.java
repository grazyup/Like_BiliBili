package com.grazy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/12 15:45
 * @Description: 已上传文件类
 */

@Data
@AllArgsConstructor
public class MyFile {

    private Long id;

    //文件上传地址
    private String url;

    //文件类型
    private String type;

    //文件md5加密字符串
    private String fileMD5;

    private Date createTime;
}
