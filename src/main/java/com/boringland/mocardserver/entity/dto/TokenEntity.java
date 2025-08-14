package com.boringland.mocardserver.entity.dto;

import lombok.Data;

@Data
public class TokenEntity {

    private String userId;
    private String token;

    public TokenEntity(String userId, String authorization) {
        this.token = authorization;
        this.userId = userId;
    }
}
