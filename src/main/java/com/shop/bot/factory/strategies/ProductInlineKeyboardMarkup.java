package com.shop.bot.factory.strategies;

import com.shop.bot.factory.InlineKeyboardMarkupFactory;
import com.shop.bot.factory.wrapper.RecordWrapper;
import com.shop.model.Product;

import java.util.List;

public class ProductInlineKeyboardMarkup extends InlineKeyboardMarkupFactory<Product> {


    public ProductInlineKeyboardMarkup(List<Product> records, int colCount) {
        super(records, colCount);
    }

    @Override
    public RecordWrapper wrapper(Product product) {
        return RecordWrapper.builder()
                .id(product.getId())
                .name(product.getName())
                .command("PRODUCT")
                .build();
    }
}
