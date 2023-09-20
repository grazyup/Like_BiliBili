package com.grazy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: grazy
 * @Date: 2023/8/29 15:15
 * @Description:  前后端统一数据响应格式 JSON格式返回
 */

@AllArgsConstructor
@Data
public class ResultResponse<T> {

    //响应码
    private String code;

    //响应信息
    private String msg;

    //响应数据
    private T data;


    public ResultResponse(String msg){
        this.code = "0";
        this.msg = msg;
        this.data = null;
    }

    public ResultResponse(String msg,T data){
        this.code = "0";
        this.msg = msg;
        this.data = data;
    }

    //不需要携带数据的成功响应
    public static <T> ResultResponse<T> success(String msg){
        return new ResultResponse<>(msg);
    }

    //需要携带数据的成功响应
    public static <T> ResultResponse<T> success(String msg,T data){
        return new ResultResponse<>(msg,data);
    }


    //不需要携带数据的失败响应
    public static <T> ResultResponse<T> error(String msg){
        ResultResponse<T> resultResponse = new ResultResponse<>(msg);
        resultResponse.setCode("500");
        return resultResponse;
    }


    //自定义响应码
    public static <T> ResultResponse<T> Custom(String code, String msg){
        ResultResponse<T> resultResponse = new ResultResponse<>(msg);
        resultResponse.setCode(code);
        return resultResponse;
    }

}
