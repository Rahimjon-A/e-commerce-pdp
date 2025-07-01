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
        for (Product product1 : products) {
            if(product1.getName().equals(product.getName())) {
                return false;
            }
        }
        products.add(product);
        this.update();
        return true;
    }

    public List<Product> getProductsByCategory(Set<UUID> catsId) {
        List<Product> catProducts = new ArrayList<>();
        for (Product product : products) {
            if(catsId.contains(product.getCategoryId())){
                catProducts.add(product);
            }
        }
        return catProducts;
    }

    public boolean deleteProductByName(String name) {
        for (Product product : products) {
            if(product.getName().equals(name)) {
                products.remove(product);
                this.update();
                return true;
            }
        }
        return false;
    }

    public String addProductAmount(String name,int amount){
        for(Product product : products) {
            if(product.getName().equals(name )&& amount > 0) {
                product.setAmount(product.getAmount() + amount);
                this.update();
                return product.getName() + " has been added new " + amount +" item.";
            }
        }
        return "Product not found";
    }

    public Product getProductByName(String name,int quantity) {
        for (Product product : products) {
            if(product.getName().equals(name) && quantity <= product.getAmount()) {
                product.setAmount(product.getAmount() - quantity);
                this.update();
                return product;
            }
        }
        return null;
    }

    public void update() {
        FileUtility.saveFileToJson(PRODUCT_FILE, products);
    }


}

