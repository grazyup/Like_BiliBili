package com.grazy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/8/29 20:29
 * @Description:  用户基本信息表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "user-infos")
public class UserInfo {

    @Id
    private Long id;

    private Long userId;

    /**
     * 昵称
     */
    @Field(type = FieldType.Text)
    private String nick;

    private String avatar;

    private String sign;

    /**
     * 0 男，1 女，2 未知
     */
    private String gender;

    private String birth;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Date)
    private Date updateTime;

    /**
     * 是否相互关注
     */
    private Boolean followed;
}
