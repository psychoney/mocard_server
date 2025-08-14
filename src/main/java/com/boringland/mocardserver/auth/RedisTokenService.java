package com.boringland.mocardserver.auth;

import com.boringland.mocardserver.entity.dto.TokenEntity;
import com.boringland.mocardserver.service.TokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisTokenService implements TokenService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirt}")
    private Long expirtTime;

    @Value("${jwt.authorization}")
    private String access_Token;

    @Override
    public String createToken(String userId) {
        String token = Jwts.builder().setId(userId).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
        //存储到redis并设置过期时间
        stringRedisTemplate.boundValueOps(access_Token + ":" + userId).set(token, expirtTime, TimeUnit.SECONDS);
        return token;
    }

    @Override
    public boolean checkToken(TokenEntity model) {
        if (model == null) {
            return false;
        }
        String token = stringRedisTemplate.boundValueOps(access_Token + ":" + model.getUserId()).get();
        if (token == null || !token.equals(model.getToken())) {
            return false;
        }
        //如果验证成功，说明此用户进行了一次有效操作，延长token的过期时间
        stringRedisTemplate.boundValueOps(model.getUserId()).expire(expirtTime,TimeUnit.SECONDS);
        return true;
    }


    @Override
    public TokenEntity getToken(String authentication) {
        return null;
    }

    @Override
    public void deleteToken(String userId) {
        stringRedisTemplate.delete(userId);
    }
}
