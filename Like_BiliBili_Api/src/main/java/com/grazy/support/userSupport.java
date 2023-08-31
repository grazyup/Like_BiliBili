package com.grazy.support;

import com.grazy.Exception.CustomException;
import com.grazy.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Author: grazy
 * @Date: 2023/8/31 14:15
 * @Description: 提供userApi功能支持
 */

@Component
public class userSupport {

    /**
     * 根据token解析获取用户id
     * @return
     */
    public Long getCurrentUserId(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取请求头中的token
        String token = requestAttributes.getRequest().getHeader("token");
        //解析token获取userid
        Long userId = TokenUtil.verifyToken(token);
        if(userId < 0){
            throw new CustomException("非法用户！");
        }
        return userId;
    }
}
