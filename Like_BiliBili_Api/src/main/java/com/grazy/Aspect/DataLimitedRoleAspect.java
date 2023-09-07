package com.grazy.Aspect;

import com.grazy.Common.AuthRoleConstant;
import com.grazy.Exception.CustomException;
import com.grazy.Service.UserRoleService;
import com.grazy.auth.UserRole;
import com.grazy.domain.UserMoment;
import com.grazy.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2023/9/8 0:06
 * @Description: 数据库角色等级限制切面
 */

@Aspect
@Order(1)
@Component
public class DataLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    //配置切入点 (将该注解作为切入点对目标方法进行”通知“的“织入”)
    @Pointcut("@annotation(com.grazy.annotation.DataLimitedRole)")
    public void check(){
    }

    /**
     *  注解切入点的前置通知
     *  实现判断用户是否有权限执行数据库内的字段操作
     * @param joinPoint 切面的信息
     */
    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        //获取角色表数据
        List<UserRole> rolesList = userRoleService.getUserRoleDate(userSupport.getCurrentUserId());
        //提取角色表中等级编码为set集合
        Set<String> roleCodeSet = rolesList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        //获取切入点目标方法的传入参数
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserMoment) {
                if (roleCodeSet.contains(AuthRoleConstant.ROLE_LV1) && !"0".equals(((UserMoment) arg).getType())) {
                    throw new CustomException("您当前账号等级不够，只能发布视频类型的动态！");
                } else if (roleCodeSet.contains(AuthRoleConstant.ROLE_LV2)
                        && (!"0".equals(((UserMoment) arg).getType()) || !"1".equals(((UserMoment) arg).getType()))) {
                    throw new CustomException("当前您的等级不够，只能发布视频或者直播类动态");
                }
            }
        }
    }

}
