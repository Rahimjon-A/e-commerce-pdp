package com.shop.bot.botController;

import com.shop.bot.factory.ReplyKeyboardMarkupFactory;
import com.shop.service.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

public class SettingsController {

    public static SendMessage handle(String text, Long chatId, Integer messageId, Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        switch (text) {
            case "Settings 🌐" -> {
                sendMessage.setText("Select an option!");
                ReplyKeyboardMarkup markup = ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("Language 🌏", "Help 🆘", "Profile 🌄", "About us ☕️", "Back ⬅️"), 2);
                sendMessage.setReplyMarkup(markup);
            }
            case "Language 🌏" -> {
                sendMessage.setText("Select an option!");
                ReplyKeyboardMarkup markup = ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("English", "Uzbek", "Russian"), 3);
                sendMessage.setReplyMarkup(markup);
            }
            case "English", "Uzbek", "Russian" -> {
                sendMessage.setText("Language is changed");
                ReplyKeyboardMarkup markup = ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("Language 🌏", "Help 🆘", "Profile 🌄", "About us ☕️", "Back ⬅️"),2);
                sendMessage.setReplyMarkup(markup);
            }
            case "Profile 🌄" -> {
                User user = update.getMessage().getFrom();
                String format = String.format("""
                        User name: %s
                        First name: %s
                        Last name: %s
                        """, user.getUserName(), user.getFirstName(), user.getLastName());
                sendMessage.setText("\t\t ABOUT THIS ACCAUNT :" + "\n" + format);
                ReplyKeyboardMarkup markup = ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("Settings 🌐", "Back ⬅️"), 2);
                sendMessage.setReplyMarkup(markup);
            }
            case "Help 🆘" -> {
                sendMessage.setText("""
            🆘 *Help Menu*

            Here’s how to use the bot:
            ▪️ Use *Menu 🔔* to get product or ordered product.
            ▪️ Use *Buckets 🛒* view products in bucket.
            ▪️ Use *Order 📋* view order history.
            ▪️ Use *Settings 🌐* exchange the language,view profile and contact the creators.
            ▪️ Press *Back ⬅️* to return to the previous menu.

            
            """);

                ReplyKeyboardMarkup markup = ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("Settings 🌐", "Back ⬅️"), 2);
                sendMessage.setReplyMarkup(markup);
            }
            case "About us ☕️" -> {
                sendMessage.setText("""
            ☕️ About Us:
            Welcome to our shop bot!
            Team members: 
            https://t.me/@RahimjonAbduraximov
            https://t.me/@vnuk_docenta
            https://t.me/@JASURBEK_7767
            https://t.me/@Alamiy_00
            https://t.me/muhammadamin_6599
            
            """);

                ReplyKeyboardMarkup markup = ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("Settings 🌐", "Back ⬅️"), 1);
                sendMessage.setReplyMarkup(markup);
            }
            case "Back ⬅️" -> {
                sendMessage.setText("⬅️ Back to main menu.");
                ReplyKeyboardMarkup markup = ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("Menu 🔔", "Buckets 🛒", "Order 📋", "Settings 🌐"), 3);


                sendMessage.setReplyMarkup(markup);
            }
            default -> {
                sendMessage.setText("Invalid command!");
                ReplyKeyboardMarkup markup = ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("Menu 🔔", "Buckets 🛒", "Order 📋", "Settings 🌐"), 3);

                sendMessage.setReplyMarkup(markup);
            }
        }

        return sendMessage;
    }
}
