package com.grazy.Exception;

import lombok.Data;

/**
 * @Author: grazy
 * @Date: 2023/8/29 16:37
 * @Description:  自定义异常
 */

@Data
public class CustomException extends RuntimeException{

    private static final Long serialVersionUID = 1L;

    private String code;

    //可自定义异常错误响应编码
    public CustomException(String code,String msg){
        super(msg);
        this.code = code;
    }

    public CustomException(String msg){
        super(msg);
        this.code = "500";
    }

}
