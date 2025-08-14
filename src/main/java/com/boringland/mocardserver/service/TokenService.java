package com.boringland.mocardserver.service;

import com.boringland.mocardserver.entity.dto.TokenEntity;

public interface TokenService {

    /**
     * 创建一个token关联上指定用户
     *
     * @param userId 指定用户的id
     * @return 生成的token
     */
    String createToken(String userId);

    /**
     * 检查token是否有效
     *
     * @param model token
     * @return 是否有效
     */
    boolean checkToken(TokenEntity model);

    /**
     * 从字符串中解析token
     *
     * @param authentication 加密后的字符串
     * @return Token实例
     */
    TokenEntity getToken(String authentication);

    /**
     * 清除token
     *
     * @param userId 登录用户的id
     */
    void deleteToken(String userId);

}
