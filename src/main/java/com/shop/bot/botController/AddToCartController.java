package com.shop.bot.botController;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.UUID;

public class AddToCartController {

    public static InlineKeyboardMarkup createInlineBtns(UUID productId, String quantity) {
        InlineKeyboardButton minus = new InlineKeyboardButton("➖");
        minus.setCallbackData("DECREASE" + productId);

        InlineKeyboardButton count = new InlineKeyboardButton(String.valueOf(quantity));
        count.setCallbackData("COUNT" + productId);

        InlineKeyboardButton plus = new InlineKeyboardButton("➕");
        plus.setCallbackData("INCREASE" + productId);

        InlineKeyboardButton addToCart = new InlineKeyboardButton("🛒 Add to Cart");
        addToCart.setCallbackData("ADD_TO_CART" + productId);

        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        inline.setKeyboard(List.of(
                List.of(minus, count, plus),
                List.of(addToCart)
        ));

        return inline;
    }
}
