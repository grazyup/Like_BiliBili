package com.grazy.Exception;

/**
 * @Author: grazy
 * @Date: 2023/8/29 16:37
 * @Description:  自定义异常
 */

public class CustomException extends RuntimeException{

    private static final Long serialVersionUID = 1L;

    private Integer code;

    //可自定义异常错误响应编码
    public CustomException(Integer code,String msg){
        super(msg);
        this.code = code;
    }

    public CustomException(String msg){
        super(msg);
        this.code = 500;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
