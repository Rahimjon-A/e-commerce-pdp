package com.shop;

import com.shop.bot.botService.TelegramBotService;
import com.shop.enums.Role;
import com.shop.model.*;
import com.shop.service.CardService;
import com.shop.service.CategoryService;
import com.shop.service.ProductService;
import com.shop.service.UserService;
import com.shop.utility.DateUtility;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;

public class Main {

    static Scanner scannerInt = new Scanner(System.in);
    static Scanner scannerStr = new Scanner(System.in);
    static UserService userService = new UserService();
    static ProductService productService = new ProductService();
    static CategoryService categoryService = new CategoryService();
    static CardService cardService = new CardService();

    public static void main(String[] args) {

        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new TelegramBotService());
            System.out.println("Bot is working!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        int stepCode = 10;
        while (stepCode != 0) {

            System.out.println("""
                    1. Register
                    2. Login
                    3. Exit
                    """);

            switch (scannerInt.nextInt()) {
                case 1 -> {

                    System.out.print("Enter your full name: ");
                    String fullName = scannerStr.nextLine();
                    System.out.print("Enter your user name: ");
                    String userName = scannerStr.nextLine();
                    System.out.print("Enter phone number: ");
                    String phone = scannerStr.nextLine();
                    System.out.print("Enter password: ");
                    String password = scannerStr.nextLine();

                    boolean res = userService.register(new User(fullName, userName, phone, password));
                    System.out.println("Register " + (res ? "Successfully" : "failed"));
                }
                case 2 -> {

                    System.out.print("Enter user name: ");
                    String phone = scannerStr.nextLine();
                    System.out.print("Enter password: ");
                    String password = scannerStr.nextLine();

                    User currUser = userService.login(phone, password);

                    if (currUser != null) {
                        System.out.println("Logged in!");
                        if (currUser.getRole() == Role.USER) {
                            userMenu(currUser);
                        } else {
                            adminMenu(currUser);
                        }
                    } else {
                        System.out.println("User not found");
                    }
                }
                case 3 -> {
                    stepCode = 0;
                }
            }
        }
    }

    private static void adminMenu(User currUser) {

        int stepCode = 10;
        while (stepCode != 0) {
            System.out.println("");
            System.out.println("""
                    1. Add category
                    2. Get Child Categories By Id
                    3. Delete Category
                    4. Add product
                    5. Add amount for a product
                    6. Get Products By Category
                    7. Delete Product
                    8. List of Category
                    9. List of Products
                    0. Exit
                    """);
            switch (scannerInt.nextInt()) {

                case 1 -> {
                    addCategory(currUser);
                }
                case 2 -> {
                    getAllCategories();
                }
                case 3 -> {

                    UUID id = chooseParentCategory();
                    boolean res = categoryService.deleteCategory(id);
                    System.out.println("category is " + (res ? "deleted!" : "not deleted!"));
                }
                case 4 -> {
                    addProduct(currUser);
                }
                case 5 -> {

                    boolean hasProduct = listOfProducts();

                    if (hasProduct) {
                        System.out.print("Enter product name: ");
                        String productName = scannerStr.nextLine();
                        System.out.print("Enter product amount: ");
                        int productAmount = scannerInt.nextInt();

                        String res = productService.addProductAmount(productName, productAmount);
                        System.out.println(res);
                    }

                }
                case 6 -> {
                    getProductsByCategoryId();
                }
                case 7 -> {
                    deleteProduct(currUser);
                }
                case 8 -> {
                    listOfCategories();
                }
                case 9 -> {
                    listOfProducts();

                }
                case 0 -> {
                    stepCode = 0;
                }
                default -> {
                    System.out.println("Something went wrong");
                }
            }
        }

    }

    private static boolean listOfProducts() {
        List<Product> products = productService.getProducts();
        if (products.isEmpty()) {
            System.out.println("No product yet!");
            return false;
        } else {
            System.out.println("Products list:");
            System.out.println("——————————————————");
            products.forEach(product -> {
                Category category = categoryService.getCategoryById(product.getCategoryId());
                System.out.printf("""
                                Name: %s
                                Price: %S
                                Amount: %d
                                CreatedBy: %s
                                CreatedAt: %s
                                Category: %s
                                ——————————————————
                                \n""",
                        product.getName(),
                        product.getPrice(),
                        product.getAmount(),
                        product.getCreatedBy(),
                        DateUtility.formatMyDate(product.getCreatedAt()),
                        (category != null ? category.getCatName() : "Head"));
            });
            return true;
        }
    }

    private static void getAllCategories() {

        System.out.println("""
                1. only child categories
                2. child categories with sub categories 
                """);
        int choice = scannerInt.nextInt();
        listOfCategories();
        System.out.print("Enter category 'ID': ");
        String id = scannerStr.nextLine();

        try {
            UUID categoryId = UUID.fromString(id);
            if (choice == 1) {
                List<Category> categories = categoryService.getChildCategories(categoryId);
                if (categories.isEmpty()) {
                    System.out.println("This category has no sub category :(");
                } else {
                    System.out.println("Sub categories: ");
                    for (Category category : categories) {
                        System.out.println("- " + category.getCatName());
                    }
                }
            } else if (choice == 2) {
                Set<UUID> allSubCategories = categoryService.getSubCategories(categoryId);
                System.out.println("All subcategories: ");
                for (Category category : categoryService.getCategories()) {
                    if (allSubCategories.contains(category.getCatId()) && !category.getCatId().equals(categoryId)) {
                        System.out.println("- " + category.getCatName());
                    }
                }
            } else {
                System.out.println("Invalid choice");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format! Please enter correct category ID");
        }
    }

    private static void deleteProduct(User currUser) {
        listOfProducts();
        System.out.print("Enter product name: ");
        boolean res = productService.deleteProductByName(scannerStr.nextLine());
        System.out.println("Product is " + (res ? "deleted!" : "not found"));

    }

    private static void listOfCategories() {
        List<Category> categories = categoryService.getCategories();
        System.out.println("\nCATEGORIES:");
        System.out.println("================================================");

        categories.forEach(category -> {
            System.out.printf("""
                            —— %s (ID: %s)
                               created by: %s
                               created at: %s
                               ————————————————————————————————————————————————
                            """,
                    category.getCatName(),
                    category.getCatId(),
                    category.getCreatedBy(),
                    DateUtility.formatMyDate(category.getCreatedAt()));
        });
        System.out.println("================================================");
    }


    private static void getProductsByCategoryId() {
        UUID catId = chooseParentCategory();
        Set<UUID> catsId = categoryService.getSubCategories(catId);
        Category category = categoryService.getCategoryById(catId);
        List<Product> products = productService.getProductsByCategory(catsId);

        if (products.isEmpty()) {
            System.out.printf("%s category is empty\n", (category != null ? category.getCatName() : "Head"));
        } else {
            System.out.println("Products list");
            System.out.println("------------------");
            //   products.stream().forEach((System.out::println));

            products.forEach(product -> {
                Category productCat = categoryService.getCategoryById(product.getCategoryId());
                System.out.printf("""
                                Name: %s
                                Price: %S
                                Amount: %d
                                Category: %s
                                ------------------
                                \n""", product.getName(),
                        product.getPrice(),
                        product.getAmount(),
                        (productCat != null ? productCat.getCatName() : "Head"));
            });
        }
    }

    private static void addProduct(User currUser) {
        UUID catId = chooseParentCategory();

        System.out.print("Enter product name: ");
        String name = scannerStr.nextLine();

        System.out.print("Enter price: ");
        double price = scannerInt.nextDouble();

        System.out.print("Enter amount: ");
        int amount = scannerInt.nextInt();

        boolean res = productService.addProduct(new Product(name, price, amount, catId, currUser.getUserName(), new Date()));
        System.out.println("Product is " + (res ? "added!" : "not added!"));
    }

    private static void addCategory(User currUser) {
        UUID parentId = chooseParentCategory();

        System.out.print("Enter new category name: ");
        String catName = scannerStr.nextLine();

        boolean added = categoryService.addCategory(new Category(catName, currUser.getUserName(), new Date(), parentId));
        System.out.println("Category " + (added ? "added!" : "failed (duplicate?)"));
    }

    private static UUID chooseParentCategory() {
        UUID currId = null;
        Stack<UUID> showCategories = new Stack<>();

        while (true) {
            UUID finalId = currId;
            List<Category> categories = categoryService.getCategories();
            List<Category> children = categories.stream()
                    .filter(category -> Objects.equals(category.getParentId(), finalId))
                    .toList();

            Category currCat = categoryService.getCategoryById(finalId);

            if (currCat == null) {
                System.out.println("0. Head (current)");
            } else {
                System.out.printf("0. %s (current)\n", currCat.getCatName());
            }

            if (!children.isEmpty()) {
                System.out.println("Sub categories");
                int c = 1;
                for (Category child : children) {
                    System.out.println(c++ + ". " + child.getCatName());
                }
            }
            System.out.println("99. ⏎ (back)");
            System.out.print("Select category: ");
            int res = scannerInt.nextInt();

            if (res == 0) {
                showCategories = null;
                return currId;
            }

            if (res == 99) {
                if (showCategories.isEmpty()) {
                    currId = null;
                } else {
                    currId = showCategories.pop();
                }
            } else {
                int idx = res - 1;
                if (idx >= 0 && idx < children.size()) {
                    showCategories.push(currId);
                    currId = children.get(idx).getCatId();
                } else {
                    System.out.println("Invalid command!");
                }

            }
        }
    }

    private static void userMenu(User currUser) {
        int stepCode = 10;
        while (stepCode != 0) {
            System.out.println("""
                    1. Add to card
                    2. List of cards
                    3. Order from Card
                    4. Orders List
                    5. Exit
                    """);
            switch (scannerInt.nextInt()) {
                case 1 -> {
                    shoppingCard(currUser);
                }
                case 2 -> {
                    List<Card> userCards = cardService.getCardsByUserId(currUser.getUserId());

                    if (userCards.isEmpty()) {
                        System.out.println("You don't have cards yet!");
                    } else {
                        System.out.println("YOUR CARDS");
                        for (Card userCard : userCards) {
                            System.out.println(userCard);
                        }
                    }
                }
                case 3 -> {
                    cardService.getCardsByUserId(currUser.getUserId());
                    orderedProduct(currUser);
                }
                case 4 -> {
                    List<Card> orders = cardService.getOrdersByUserId(currUser.getUserId());
                    System.out.printf("User: %s \n", currUser.getUserName());
                    System.out.println("Orders: ");
                    int c = 1;
                    for (Card order : orders) {
                        System.out.printf("""
                                      №: %d
                                      ------------------
                                """, c++);

                        order.getOrders().forEach(order1 -> {
                            System.out.printf("""
                                                prductName: %s
                                                price: %s
                                                quantity: %s
                                                --------------
                                            """, order1.getProductName(),
                                    order1.getPrice(),
                                    order1.getQuantity());
                        });
//
//                        for (Order orderOrder : order.getOrders()) {
//                            System.out.printf("""
//                                        prductName: %s
//                                        price: %s
//                                        quantity: %s
//                                        --------------
//                                    """, orderOrder.getProductName(),
//                                    orderOrder.getPrice(),
//                                    orderOrder.getQuantity());
//                        }
                    }
                }
                case 5 -> {
                    stepCode = 0;
                }
                default -> {
                    System.out.println("Something went wrong");
                }
            }
        }
    }

    private static void orderedProduct(User currUser) {
        List<Card> cards = cardService.getCardsByUserId(currUser.getUserId());
        int c = 1;
        for (Card card : cards) {
            double totalPrice = 0;
            System.out.printf("""
                            Order №: %s
                            userName: %s
                            ————————————————————
                            """, c++,
                    currUser.getUserName());
            System.out.println("Products: ");
            for (Order order : card.getOrders()) {
                totalPrice += order.getPrice() * order.getQuantity();
                System.out.printf("""
                            productName: %s
                            productPrice: %s
                            productQuantity: %s
                            ————————————————————
                        """, order.getProductName(), order.getPrice(), order.getQuantity());
            }
            System.out.println("Total price: " + totalPrice);
            System.out.println("=======================\n");
        }

        System.out.print("Enter order number: ");
        c = scannerInt.nextInt();
        int idx = c - 1;
        if (idx < 0 || idx < cards.size()) {
            Card card = cards.get(idx);
            card.setOrder(true);
            cardService.update();
            System.out.println("Ordered");
        } else {
            System.out.println("Invalid command");
        }
    }

    private static void shoppingCard(User currUser) {
        Card newCard = new Card();
        newCard.setUserId(currUser.getUserId());

        while (true) {
            UUID catId = chooseParentCategory();
            Set<UUID> catsId = categoryService.getSubCategories(catId);
            Category category = categoryService.getCategoryById(catId);
            List<Product> products = productService.getProductsByCategory(catsId);

            if (products.isEmpty()) {
                System.out.println("This category is empty :(");
            } else {
                int c = 1;
                for (Product product : products) {
                    System.out.printf("""
                            № %d | %s
                            price: %s
                            remaining: %d
                            ——————————————————
                            """, c++, product.getName(), product.getPrice(), product.getAmount());
                }
                System.out.print("Choose product (enter product name): ");
                String name = scannerStr.nextLine();

                System.out.print("How many or How Much do you want: ");
                int quantity = scannerInt.nextInt();

                Product seletctedProduct = productService.getProductByName(name, quantity);


                if (seletctedProduct != null) {
                    newCard.getOrders().add(new Order(seletctedProduct, quantity, new Date(), currUser.getUserName()));
                    System.out.println("Product is added to bucket!");
                } else {
                    System.out.println("No product found or not enough product:(");
                }

            }

            System.out.println("""
                    Continue shopping?
                    1. Yes
                    2. No
                    """);
            int res = scannerInt.nextInt();
            if (res == 2) break;
        }

        if (!newCard.getOrders().isEmpty()) {
            cardService.addCard(newCard);
        }


    }
}