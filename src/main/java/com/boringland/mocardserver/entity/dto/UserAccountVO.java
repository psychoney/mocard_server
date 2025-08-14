package com.boringland.mocardserver.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserAccountVO implements Serializable {

    /**
     * 用户id
     */
    private Integer id;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 会员标志
     */
    private String memberFlag;

    /**
     * 用户状态
     */
    private String status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLogin;

    /**
     * token
     */
    private String token;
}
