package com.boringland.mocardserver.aspect;

import com.boringland.mocardserver.constant.AuthConstant;
import com.boringland.mocardserver.exception.NonMembershipException;
import com.boringland.mocardserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
@Slf4j
public class MembershipAspect {

    @Autowired
    private UserService userService;

    @Before("@annotation(com.boringland.mocardserver.annotation.CheckMembership)")
    public void checkMembership(JoinPoint joinPoint) throws NonMembershipException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uniqueID = (String) request.getAttribute(AuthConstant.CURRENT_USER_ID);
        if (!userService.isVIP(uniqueID)) {
            log.info("非会员用户请求: {}", uniqueID);
            if (userService.limitCheckCanUse(uniqueID)) {
                log.info("非会员用户请求可以使用: {}", uniqueID);
//                userService.addUsageCount(uniqueID); 修改移动到openai返回结果处理中
            }else {
                log.info("非会员用户请求次数超限: {}", uniqueID);
                throw new NonMembershipException("Request limit exceeded for non-member users");
            }
        }
    }

}
