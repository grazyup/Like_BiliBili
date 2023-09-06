package com.grazy.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: grazy
 * @time: 2023/9/16
 * @description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDetail {
    private Long id;

    private String refreshToken;

    private Long userId;

    private Date createTime;
}
