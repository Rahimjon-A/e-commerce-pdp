package com.shop.service;

import com.shop.model.Product;
import com.shop.utility.FileUtility;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class ProductService {
    private static final String PRODUCT_FILE = "./files/products.json";
    private final List<Product> products;


    public ProductService() {
        products = FileUtility.loadFileFromJson(PRODUCT_FILE, Product.class);
    }

    public boolean addProduct(Product product) {
        boolean isHave = products.stream()
                .anyMatch(product1 -> product1.getName().equals(product.getName()));

        if (isHave) return false;

        products.add(product);
        this.update();
        return true;
    }

    public List<Product> getProductsByCategory(Set<UUID> catsId) {
        return products.stream()
                .filter(product -> catsId.contains(product.getCategoryId())).toList();
    }

    public boolean deleteProductByName(String name) {
        boolean b = products.removeIf(product -> product.getName().equals(name));
        if (b) this.update();
        return b;
    }

    public String addProductAmount(String name, int amount) {
        return products.stream()
                .filter(product -> product.getName().equals(name) && amount > 0)
                .findFirst()
                .map(product -> {
                    product.setAmount(product.getAmount() + amount);
                    this.update();
                    return product.getName() + " has been added new " + amount + " item.";
                })
                .orElse("Product not found");

    }

    public Product getProductByName(String name, int quantity) {
       return products.stream()
                .filter(product -> product.getName().equals(name)&& quantity <= product.getAmount())
                .findFirst()
                .map(product -> {
                    product.setAmount(product.getAmount() - quantity);
                    this.update();
                    return product;
                }).orElse(null);
    }

    public void  deleteCategoryProducts(Set<UUID> ids) {
        products.removeIf(product -> ids.contains(product.getCategoryId()));
        this.update();
    }

    public Product getProductById(UUID id) {
        return products.stream()
                .filter((p) -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void update() {
        FileUtility.saveFileToJson(PRODUCT_FILE, products);
    }


}

