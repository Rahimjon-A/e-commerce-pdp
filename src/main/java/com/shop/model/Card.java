package com.shop.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Card {
    private UUID userId;
    private boolean isOrder;
    private List<Order> orders;
    private UUID cardId;

    public  Card() {
        cardId = UUID.randomUUID();
        orders = new ArrayList<>();
        isOrder = false;
    }
}
