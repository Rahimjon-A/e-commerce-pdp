package com.shop.bot.botService;

import com.shop.bot.botController.AddToCartController;
import com.shop.model.Card;
import com.shop.model.Order;
import com.shop.model.User;
import com.shop.service.CardService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.UUID;

public abstract class BaseBotService extends TelegramLongPollingBot {
    @Override
    public abstract void onUpdateReceived(Update update);

    protected void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void send(AnswerCallbackQuery ack) {
        try {
            execute(ack);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void send(SendPhoto photo) {
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void updateQuantityMessage(Long chatId, UUID productId, int quantity, Update update) {
        EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
        edit.setChatId(chatId);
        edit.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        edit.setReplyMarkup(AddToCartController.createInlineBtns(productId, String.valueOf(quantity)));

        try {
            execute(edit);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    protected void listOfCarts(User currUser, Long chatId) {
        CardService cardService = new CardService();

        List<Card> cardsByUserId = cardService.getCardsByUserId(currUser.getUserId());

        if (cardsByUserId.isEmpty()) {
            send(new SendMessage(chatId.toString(), "🛒 Your cart is empty."));
            return;
        } else {

            int c = 1;
            for (Card card : cardsByUserId) {
                StringBuilder productText = new StringBuilder();

                productText.append("🛒 *Cart #" + c++ + "*\n");
                productText.append("━━━━━━━━━━━━━━━━\n\n");

                double total = 0.0;

                for (Order order : card.getOrders()) {
                    productText.append("📦 *Product:* " + order.getProductName() + "\n");
                    productText.append("💰 *Price:* $" + String.format("%.2f", order.getPrice()) + "\n");
                    productText.append("🔢 *Amount:* " + order.getQuantity() + "\n");

                    double itemTotal = order.getPrice() * order.getQuantity();
                    productText.append("➖ *Subtotal:* $" + String.format("%.2f", itemTotal) + "\n\n");
                    total += itemTotal;
                }

                productText.append("━━━━━━━━━━━━━━━━\n");
                productText.append("💵 *Total:* $" + String.format("%.2f", total) + "\n");

                SendMessage cartMessage = new SendMessage();
                cartMessage.setChatId(chatId);
                cartMessage.setText(productText.toString());
                cartMessage.setParseMode("Markdown");

                InlineKeyboardMarkup inlineBtn = new InlineKeyboardMarkup();
                InlineKeyboardButton confirmBtn = new InlineKeyboardButton("✅ Confirm Order");
                confirmBtn.setCallbackData("CONFIRM_ORDER" + (c - 2));

                inlineBtn.setKeyboard(List.of(List.of(confirmBtn)));
                cartMessage.setReplyMarkup(inlineBtn);

                send(cartMessage);
            }
        }

    }

    protected void ListOfOrders(User currUser, Long chatId) {
        CardService cardService = new CardService();

        List<Card> cardsByUserId = cardService.getOrdersByUserId(currUser.getUserId());

        if (cardsByUserId.isEmpty()) {
            send(new SendMessage(chatId.toString(), "🛒 You don't have orders"));
            return;
        } else {

            int c = 1;
            for (Card card : cardsByUserId) {
                StringBuilder productText = new StringBuilder();

                productText.append("📦 *Order #" + c++ + "*\n");
                productText.append("━━━━━━━━━━━━━━━━\n\n");
                double total = 0.0;

                for (Order order : card.getOrders()) {
                    productText.append("🛍️ *Product:* " + order.getProductName() + "\n");
                    productText.append("💰 *Price:* $" + String.format("%.2f", order.getPrice()) + "\n");
                    productText.append("✖️ *Amount:* " + order.getQuantity() + "\n");

                    double itemTotal = order.getPrice() * order.getQuantity();
                    productText.append("➖ *Subtotal:* $" + String.format("%.2f", itemTotal) + "\n\n");
                    total += itemTotal;
                }

                productText.append("━━━━━━━━━━━━━━━━\n");
                productText.append("💳 *Total:* $" + String.format("%.2f", total) + "\n");
                productText.append("🟢 *Status:* Processing\n");

                SendMessage orderMessage = new SendMessage();
                orderMessage.setChatId(chatId);
                orderMessage.setText(productText.toString());
                orderMessage.setParseMode("Markdown");

                send(orderMessage);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "e_commerce_with_java_bot";
    }

    public String getBotToken() {
        return "7348042280:AAGu_hnf5S1B8WAPYH3WnxeQ9GWWqAnpcdQ";
    }
}
