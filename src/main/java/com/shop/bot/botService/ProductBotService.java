package com.shop.bot.botService;

import com.shop.bot.factory.strategies.ProductInlineKeyboardMarkup;
import com.shop.model.Product;
import com.shop.service.ProductService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Data
public class ProductBotService {
    private final ProductService productService;
    private Set<UUID> categoryIds;

    public InlineKeyboardMarkup getInlineKeyBoard() {
        return new ProductInlineKeyboardMarkup(getProducts(), 1).inlineKeyboardMarkup();
    }

    private List<Product> getProducts() {
        if(categoryIds.isEmpty()) {
            return  productService.getProducts();
        }else {
           return productService.getProductsByCategory(categoryIds);
        }
    }


}
