package com.shop.model;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@ToString
@Data
@NoArgsConstructor
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
    @Setter
    private String createdBy;
    @Setter
    private Date createdAt;

    public Product(String name, double price, int amount, UUID categoryId, String createdBy, Date createdAt) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.amount = amount;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

}

