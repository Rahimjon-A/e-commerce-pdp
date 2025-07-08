package com.shop.bot.botController;

import com.shop.bot.factory.ReplyKeyboardMarkupFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

public class SettingsController {

    public static SendMessage handle(String text, Long chatId, Integer messageId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        switch (text) {
            case "Settings 🌐" -> {
                sendMessage.setText("Select an option!");
                ReplyKeyboardMarkup markup = ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("Language 🌏", "Help 🆘", "Profile 🌄", "About us ☕", "Back ⬅️"), 2);
                sendMessage.setReplyMarkup(markup);
            }
            case "Language 🌏" -> {
            }
            case "Profile 🌄" -> {
            }
            case "About us ☕" -> {
            }
            case "Back ⬅️" -> {
            }
            default -> {
            }
        }

        return sendMessage;
    }
}
