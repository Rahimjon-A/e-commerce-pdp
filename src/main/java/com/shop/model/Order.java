package com.shop.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Order {
    private UUID productId;
    private String productName;
    private double price;
    private int quantity;

    public Order(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.price = product.getPrice();
        this.quantity = quantity;
    }

}
