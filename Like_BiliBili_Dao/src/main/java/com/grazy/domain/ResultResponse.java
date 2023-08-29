package com.grazy.domain;

/**
 * @Author: grazy
 * @Date: 2023/8/29 15:15
 * @Description:  前后端统一数据响应格式 JSON格式返回
 */

public class ResultResponse<T> {

    //响应码
    private Integer code;

    //响应信息
    private String msg;

    //响应数据
    private T data;


    public ResultResponse(String msg){
        this.code = 200;
        this.msg = msg;
        this.data = null;
    }

    public ResultResponse(String msg,T data){
        this.code = 200;
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
        resultResponse.setCode(500);
        return resultResponse;
    }


    //自定义响应码
    public static <T> ResultResponse<T> Custom(Integer code, String msg){
        ResultResponse<T> resultResponse = new ResultResponse<>(msg);
        resultResponse.setCode(code);
        return resultResponse;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
