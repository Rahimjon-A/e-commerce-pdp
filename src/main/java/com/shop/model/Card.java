
package com.shop.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Card {
    private UUID userId;
    private List<Order> orders;

    public  Card() {
        orders = new ArrayList<>();
    }
}
