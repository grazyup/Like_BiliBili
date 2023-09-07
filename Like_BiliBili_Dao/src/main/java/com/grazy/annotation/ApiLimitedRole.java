package com.grazy.annotation;

/**
 * @Author: grazy
 * @Date: 2023/9/7 16:41
 * @Description: 接口访问角色限制自定义注解
 */

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Component
public @interface ApiLimitedRole {

    //限制接口访问的角色编码
    String[] limitedRoleCodeList() default {};
}
