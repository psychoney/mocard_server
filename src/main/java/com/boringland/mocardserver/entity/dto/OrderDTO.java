package com.boringland.mocardserver.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDTO implements Serializable {
    private String orderType;
}