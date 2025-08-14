package com.boringland.mocardserver.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class NotificationDTO implements Serializable {
    private String transactionId;
    private String productId;
    private String originalTransactionId;
    private String receiptInfo;
    private Integer orderId;
    private String deviceId;
    private String notificationType;
    private String environment;
    private Long originalPurchaseDateMs;
    private Long expiresDateMs;
    private Long purchaseDateMs;
}
