package com.boringland.mocardserver.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReceiptDTO implements Serializable {
    private String receipt;
    private Integer orderId;
    private String transactionId;
    private String productId;
    private String purchaseStatus;
}