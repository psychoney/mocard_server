package com.boringland.mocardserver.entity.dto;

import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.Serializable;

@Data
public class HandleReceiptVO implements Serializable {
    private boolean success;
    private Integer orderId;
}