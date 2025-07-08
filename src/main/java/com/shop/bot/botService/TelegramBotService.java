package com.shop.bot.botService;

import com.shop.bot.botController.AddToCartController;
import com.shop.bot.botController.SettingsController;
import com.shop.bot.botUtil.BotUtil;
import com.shop.bot.factory.ReplyKeyboardMarkupFactory;
import com.shop.enums.BotState;
import com.shop.enums.Role;
import com.shop.model.Card;
import com.shop.model.Order;
import com.shop.model.Product;
import com.shop.model.User;
import com.shop.service.CardService;
import com.shop.service.CategoryService;
import com.shop.service.ProductService;
import com.shop.service.UserService;
import com.shop.utility.DateUtility;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.*;

public class TelegramBotService extends BaseBotService {
    private final Map<Long, BotState> userStates = new HashMap<>();
    private final Map<Long, String> tempUsernames = new HashMap<>();
    private final Map<Long, User> tempUsers = new HashMap<>();
    private User currUser = null;
    private final Map<Long, Integer> productQuantities = new HashMap<>();
    private Card userCard = new Card();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            org.telegram.telegrambots.meta.api.objects.User from = update.getMessage().getFrom();

            UserService userService = new UserService();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            if (text.equals("/start")) {
                userStates.put(chatId, BotState.AWAITING_COMMAND);

                SendMessage sendMessage1 = new SendMessage();
                sendMessage1.setChatId(chatId);
                sendMessage1.setText("🎉🎊  Welcome to our E-commerce bot  🎉🎊!  Please choose:");

                ReplyKeyboardMarkup markup = ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("Login 🔄️", "Register 🆕"), 2
                );
                sendMessage1.setReplyMarkup(markup);
                send(sendMessage1);
            }
            else if (text.equals("Login 🔄️") && userStates.get(chatId) == BotState.AWAITING_COMMAND) {
                userStates.put(chatId, BotState.AWAITING_LOGIN_USERNAME);
                send(new SendMessage(chatId.toString(), "Please enter your username:"));
            }
            else if (userStates.get(chatId) == BotState.AWAITING_LOGIN_USERNAME) {
                tempUsernames.put(chatId, text);
                userStates.put(chatId, BotState.AWAITING_LOGIN_PASSWORD);
                send(new SendMessage(chatId.toString(), "Enter your password:"));
            }
            else if (userStates.get(chatId) == BotState.AWAITING_LOGIN_PASSWORD) {
                String username = tempUsernames.get(chatId);

                User user = userService.login(username, text);
                currUser = user;
                tempUsers.put(chatId, user);

                if (user != null) {
                    userStates.put(chatId, BotState.AUTHENTICATED);
                    send(new SendMessage(chatId.toString(), "✅ Login successful!"));

                    SendMessage menu = new SendMessage(chatId.toString(), "Welcome back! " + currUser.getFullName());
                    menu.setReplyMarkup(ReplyKeyboardMarkupFactory.replyKeyboardMarkup(List.of("Menu 🔔", "Buckets 🛒", "Order 📋", "Settings 🌐"), 3));
                    send(menu);
                } else {
                    userStates.put(chatId, BotState.AWAITING_COMMAND);
                    send(new SendMessage(chatId.toString(), "❌ Login failed. Try again with /start."));
                }

                tempUsernames.remove(chatId);
            }
            else if (text.equals("Register 🆕") && userStates.get(chatId) == BotState.AWAITING_COMMAND) {
                userStates.put(chatId, BotState.AWAITING_REGISTER_FULLNAME);
                send(new SendMessage(chatId.toString(), "Please enter your Full name: "));
            }
            else if (userStates.get(chatId) == BotState.AWAITING_REGISTER_FULLNAME) {
                userStates.put(chatId, BotState.AWAITING_REGISTER_PHONE);

                User user = new User();
                user.setFullName(text);
                tempUsers.put(chatId, user);

                send(new SendMessage(chatId.toString(), "Please enter your phone number: "));
            }
            else if (userStates.get(chatId) == BotState.AWAITING_REGISTER_PHONE) {
                userStates.put(chatId, BotState.AWAITING_REGISTER_USERNAME);

                User user = tempUsers.get(chatId);
                user.setPhoneNumber(text);

                send(new SendMessage(chatId.toString(), "Please choose username: "));
            }
            else if (userStates.get(chatId) == BotState.AWAITING_REGISTER_USERNAME) {
                userStates.put(chatId, BotState.AWAITING_REGISTER_PASSWORD);

                User user = tempUsers.get(chatId);
                user.setUserName(text);

                send(new SendMessage(chatId.toString(), "Please create a new password:"));
            }
            else if (userStates.get(chatId) == BotState.AWAITING_REGISTER_PASSWORD) {

                User user = tempUsers.get(chatId);
                user.setPassword(text);
                user.setRole(Role.USER);

                boolean register = userService.register(user);

                if (register) {
                    userStates.put(chatId, BotState.AUTHENTICATED);
                    send(new SendMessage(chatId.toString(), "✅ Registered successful!"));

                    SendMessage menu = new SendMessage(chatId.toString(), "Welcome!");
                    menu.setReplyMarkup(ReplyKeyboardMarkupFactory.replyKeyboardMarkup(List.of("Menu 🔔", "Buckets 🛒", "Order 📋", "Settings 🌐"), 2));
                    send(menu);
                } else {
                    userStates.put(chatId, BotState.AWAITING_REGISTER_USERNAME);
                    String formatted = "❌ Register failed. User with %s username is already exits. Try again with different one \n Enter new username: ".formatted(user.getUserName());
                    send(new SendMessage(chatId.toString(), formatted));
                }
            }
            else if (text.equals("Menu 🔔")) {
                sendMessage.setText("Choose Category 🤖");
                CategoryService categoryService = new CategoryService();
                CategoryBotService categoryBotService = new CategoryBotService(categoryService);
                sendMessage.setReplyMarkup(categoryBotService.getInlineKeyBoard());
                send(sendMessage);
            }
            else if (text.equals("Close bucket ❌")) {
                CardService cardService = new CardService();
                cardService.addCard(userCard);
                userCard = new Card();

                SendMessage menu = new SendMessage(chatId.toString(), "Added to Cards");
                menu.setReplyMarkup(ReplyKeyboardMarkupFactory.replyKeyboardMarkup(List.of("Menu 🔔", "Buckets 🛒", "Order 📋", "Settings 🌐"), 3));
                send(menu);
            }
            else if (text.equals("Buckets 🛒")) {
                listOfCarts(currUser, chatId);
            }
            else if (text.equals("Order 📋")) {
                ListOfOrders(currUser, chatId);
            }
            else if (text.equals("Settings 🌐")
                    || text.equals("Language 🌏")
                    || text.equals("Help 🆘")
                    || text.equals("Profile 🌄")
                    || text.equals("About us ☕")
                    || text.equals("Back ⬅️")) {
                send(SettingsController.handle(text, chatId, update.getMessage().getMessageId()));
            }
        }
        if (update.hasCallbackQuery()) {
            var callbackQuery = update.getCallbackQuery();
            var message = callbackQuery.getMessage();
            Long chatId = message.getChatId();
            Integer messageId = message.getMessageId();
            String data = callbackQuery.getData();

            userCard.setUserId(currUser.getUserId());

            ProductService productService = new ProductService();
            CategoryService categoryService = new CategoryService();
            CategoryBotService categoryBotService = new CategoryBotService(categoryService);
            String resText = "Head";

            if (data.startsWith("CATEGORY")) {
                String id = data.substring(8);
                UUID catId = UUID.fromString(id);
                resText = categoryService.getCategoryById(catId).getCatName();

                categoryBotService.setParentId(catId);

                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId.toString());
                editMessage.setMessageId(messageId);
                editMessage.setText("Choose Category 🤖! Now you are in " + resText + " category");
                editMessage.setReplyMarkup(categoryBotService.getInlineKeyBoard());

                try {
                    execute(editMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (data.startsWith("PRODUCTS")) {
                String id = data.substring(8);

                ProductBotService productBotService = new ProductBotService(productService);

                Set<UUID> subCategories;

                if (id.equals("null")) {
                    subCategories = Set.of();
                } else {
                    UUID categoryId = UUID.fromString(id);
                    subCategories = categoryService.getSubCategories(categoryId);
                }
                productBotService.setCategoryIds(subCategories);

                SendMessage productMessage = new SendMessage();
                productMessage.setChatId(chatId);
                productMessage.setText("📦 Products in this category:");
                productMessage.setReplyMarkup(productBotService.getInlineKeyBoard());
                send(productMessage);
            }
            else if (data.startsWith("PRODUCT")) {
                String id = data.substring(7);
                UUID productId = UUID.fromString(id);
                Product product = productService.getProductById(productId);

                File imageFile = BotUtil.createImagePath(product.getName());
                SendPhoto photo = new SendPhoto();
                photo.setChatId(chatId);
                photo.setPhoto(new InputFile(imageFile));

                StringBuilder productInfo = new StringBuilder("📦 *" + product.getName() + "*\n");
                productInfo.append("💵 Price:  ").append(product.getPrice()).append(" $\n");
                productInfo.append("📄 Amount:  ").append(product.getAmount()).append("\n");
                productInfo.append("⌚ Created date:  ").append(DateUtility.formatMyDate(product.getCreatedAt())).append("\n");

                photo.setCaption(productInfo.toString());
                photo.setParseMode("Markdown");

                int quantity = productQuantities.getOrDefault(chatId, 1);
                photo.setReplyMarkup(AddToCartController.createInlineBtns(productId, String.valueOf(quantity)));
                send(photo);

            }
            else if (data.startsWith("ADD_TO_CART")) {
                UUID productId = UUID.fromString(data.substring(11));
                Product product = productService.getProductById(productId);
                int quantity = productQuantities.getOrDefault(chatId, 1);

                userCard.getOrders().add(new Order(product, quantity, new Date(), currUser.getUserName()));
                productQuantities.remove(chatId);

                AnswerCallbackQuery ack = new AnswerCallbackQuery();
                ack.setCallbackQueryId(update.getCallbackQuery().getId());
                ack.setText("✅ Added " + quantity + " to cart!");
                ack.setShowAlert(false);
                send(ack);

                SendMessage newMenu = new SendMessage();
                newMenu.setChatId(chatId);
                newMenu.setText("🛍 Product added! What would you like to do next?");
                newMenu.setReplyMarkup(ReplyKeyboardMarkupFactory.replyKeyboardMarkup(
                        List.of("Menu 🔔", "Buckets 🛒", "Close bucket ❌", "Order 📋", "Settings 🌐"), 2
                ));
                send(newMenu);


            }
            else if (data.startsWith("INCREASE")) {
                UUID productId = UUID.fromString(data.substring(8));
                int current = productQuantities.getOrDefault(chatId, 1);
                current++;
                productQuantities.put(chatId, current);

                updateQuantityMessage(chatId, productId, current, update);
            }
            else if (data.startsWith("DECREASE")) {
                UUID productId = UUID.fromString(data.substring(8));
                int current = productQuantities.getOrDefault(chatId, 1);
                if (current > 1) {
                    current--;
                    productQuantities.put(chatId, current);
                }

                updateQuantityMessage(chatId, productId, current, update);
            }
            else if (data.startsWith("CONFIRM_ORDER")) {

                String idx = data.substring(13);
                int index = Integer.parseInt(idx);

                CardService cardService = new CardService();
                List<Card> cardsByUserId = cardService.getCardsByUserId(currUser.getUserId());
                cardsByUserId.get(index).setOrder(true);
                cardService.update();
                send(new SendMessage(chatId.toString(), "Cart number " + (index + 1) + " is ordered!"));
            }
            else if (data.startsWith("BACK")) {
                String id = data.substring(4);

                UUID backParentId = id.equals("null") ? null : UUID.fromString(id);
                if (backParentId != null) {
                    resText = categoryService.getCategoryById(backParentId).getCatName();
                }

                categoryBotService.setParentId(backParentId);

                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId.toString());
                editMessage.setMessageId(messageId);
                editMessage.setText("Choose Category 🤖! Now you are in " + resText + " category");
                editMessage.setReplyMarkup(categoryBotService.getInlineKeyBoard());

                try {
                    execute(editMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
