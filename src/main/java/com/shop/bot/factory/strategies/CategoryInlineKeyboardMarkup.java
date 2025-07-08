package com.shop.bot.factory.strategies;

import com.shop.bot.factory.InlineKeyboardMarkupFactory;
import com.shop.bot.factory.wrapper.RecordWrapper;
import com.shop.model.Category;

import java.util.List;

public class CategoryInlineKeyboardMarkup extends InlineKeyboardMarkupFactory<Category> {

    public CategoryInlineKeyboardMarkup(List<Category> records, int colCount) {
        super(records, colCount);
    }

    @Override
    public RecordWrapper wrapper(Category category) {
        return RecordWrapper.builder()
                .id(category.getCatId())
                .name(category.getCatName())
                .command("CATEGORY")
                .build();
    }
}
