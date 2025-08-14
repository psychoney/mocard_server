package com.boringland.mocardserver.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserFewshotVO implements Serializable {
    private static final long serialVersionUID = 7026951623698219901L;
    String uid;
    String deviceId;
    String cardId;
    String imageUrl;
    String question;
    String answer;
    String type;
    LocalDateTime createTime;
}
