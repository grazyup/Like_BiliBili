package com.grazy.Aspect;

import com.grazy.Exception.CustomException;
import com.grazy.Service.UserRoleService;
import com.grazy.annotation.ApiLimitedRole;
import com.grazy.auth.UserRole;
import com.grazy.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2023/9/7 16:47
 * @Description: 接口限制访问角色切面
 */

@Aspect
@Order(1)
@Component
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    //配置切入点 (将该注解作为切入点对目标方法进行”通知“的“织入”)
    @Pointcut("@annotation(com.grazy.annotation.ApiLimitedRole)")
    public void check(){
    }

    /**
     * 注解切入点的前置通知
     *  实现判断用户是否有权限访问动态功能模块的权限
     * @param joinPoint 通知方法的信息
     * @param apiLimitedRole 限制访问注解
     */
    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole){
        //获取角色表数据
        List<UserRole> rolesList = userRoleService.getUserRoleDate(userSupport.getCurrentUserId());
        //读取注解中的角色等级编码
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        //转换为Set集合
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        //提取角色表中等级编码为set集合
        Set<String> roleCodeSet = rolesList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        //取两个集合元素的交集（执行该语句后调用者集合只会保存交集）
        roleCodeSet.retainAll(limitedRoleCodeSet);
        if(roleCodeSet.size() > 0){
            //存在限制访问权限
            throw new CustomException("你的等级不够，暂时不能发布动态！");
        }
    }


}
