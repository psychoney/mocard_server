package com.boringland.mocardserver.auth;

import com.alibaba.druid.util.StringUtils;
import com.boringland.mocardserver.annotation.NoAuth;
import com.boringland.mocardserver.constant.AuthConstant;
import com.boringland.mocardserver.entity.dto.TokenEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
@Slf4j
public class  AuthInterceptor  implements HandlerInterceptor {

    @Autowired
    private RedisTokenService tokenService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.authorization}")
    private String access_Token;

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 如果方法注明了 NoAuth，则不需要登录token验证
        if (method.getAnnotation(NoAuth.class) != null) {
            return true;
        }

        // 从header中得到token
        String authorization = request.getHeader(access_Token);
        // 验证token
        if(StringUtils.isEmpty(authorization)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authorization).getBody();
            String userId = claims.getId();
            TokenEntity model = new TokenEntity(userId, authorization);
            if (tokenService.checkToken(model)) {
                //如果token验证成功，将token对应的用户id存在request中，便于之后注入
                request.setAttribute(AuthConstant.CURRENT_USER_ID, model.getUserId());
                return true;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }


}
