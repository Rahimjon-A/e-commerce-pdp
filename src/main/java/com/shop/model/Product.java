package com.shop.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@Data
public class Product {
    private UUID id;

    @Setter
    private String name;
    @Setter
    private double price;
    @Setter
    private UUID categoryId;
    @Setter
    private int amount;

    public Product(String name, double price, int amount, UUID categoryId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.amount = amount;
    }

    public Product() {
    }
}

