package com.boringland.mocardserver.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Card implements Serializable {
    String id;
    String title;
    String description;
    List<Message> role;
    List<Message> demo;
    String imageUrl;
    String type;
    String color;

    public Card() {
        this.role = new ArrayList<>();
        this.demo = new ArrayList<>();
    }
}
