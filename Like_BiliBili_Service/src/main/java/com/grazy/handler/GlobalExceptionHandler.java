package com.grazy.handler;

import com.grazy.Exception.CustomException;
import com.grazy.domain.ResultResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * @Author: grazy
 * @Date: 2023/8/29 16:31
 * @Description:  自定义全局异常处理器
 */

@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@Order(Ordered.HIGHEST_PRECEDENCE) // 优先级最高
public class GlobalExceptionHandler {

    /**
     * 捕获自定义异常信息
     * @return 将JSON格式的异常信息响应到前端
     */
    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public ResultResponse<String> CustomExceptionHandler(CustomException e) {
        //获取异常的msg
        String message = e.getMessage();
            //捕获的是自定义的异常
            return ResultResponse.Custom(((CustomException) e).getCode(), message);
        }
}
