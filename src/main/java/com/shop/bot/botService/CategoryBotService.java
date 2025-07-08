package com.shop.bot.botService;

import com.shop.bot.factory.strategies.CategoryInlineKeyboardMarkup;
import com.shop.model.Category;
import com.shop.service.CategoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Data
public class CategoryBotService {
    private final CategoryService categoryService;
    private UUID parentId = null;

    public InlineKeyboardMarkup getInlineKeyBoard() {
        List<Category> categories = categoryService.getChildCategories(parentId);

        CategoryInlineKeyboardMarkup markupFactory = new CategoryInlineKeyboardMarkup(categories, 2);
        InlineKeyboardMarkup inlineKeyboardMarkup = markupFactory.inlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = inlineKeyboardMarkup.getKeyboard();

        // 🛍 Add Get Products Button
            InlineKeyboardButton productsBtn = new InlineKeyboardButton();
        if (parentId != null) {
            productsBtn.setText("🛍 Get Products");
            productsBtn.setCallbackData("PRODUCTS" + parentId);
            keyboard.add(List.of(productsBtn));
        } else {
            productsBtn.setText("🛍 Get All Products");
            productsBtn.setCallbackData("PRODUCTS" + parentId);
            keyboard.add(List.of(productsBtn));
        }

        // ⬅️ Back button
        if (parentId != null) {
            UUID parentOfParent = categoryService.getParentId(parentId);

            InlineKeyboardButton backButton = new InlineKeyboardButton();
            backButton.setText("⬅️ Back");
            backButton.setCallbackData("BACK" + (parentOfParent != null ? parentOfParent : "null"));
            keyboard.add(List.of(backButton));
        }

        return inlineKeyboardMarkup;
    }

}
