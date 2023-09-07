package com.grazy.annotation;

/**
 * @Author: grazy
 * @Date: 2023/9/8 0:04
 * @Description: 数据库角色限制切入点标注注解
 */

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Component
public @interface DataLimitedRole {

}
