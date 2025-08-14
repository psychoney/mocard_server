package com.boringland.mocardserver.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Message implements Serializable {
    String role;
    String content;
}
