package com.shop;

import com.shop.model.*;
import com.shop.model.wrapper.UserWrapper;
import com.shop.service.CardService;
import com.shop.service.CategoryService;
import com.shop.service.ProductService;
import com.shop.service.UserService;
import com.shop.utility.FileUtility;

import java.util.*;

public class Main {
    static Scanner scannerInt = new Scanner(System.in);
    static Scanner scannerStr = new Scanner(System.in);
    static UserService userService = new UserService();
    static ProductService productService = new ProductService();
    static CategoryService categoryService = new CategoryService(productService);
    static CardService cardService = new CardService();

    public static void main(String[] args) {

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
                    System.out.println("Register " + (res ? "Successfull" : "failed"));
                }
                case 2 -> {
                    System.out.print("Enter user name: ");
                    String phone = scannerStr.nextLine();
                    System.out.print("Enter password: ");
                    String password = scannerStr.nextLine();

                    User currUser = userService.login(phone, password);
                    if (currUser != null) {
                        System.out.println("Loged in!");
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
                    listOfProducts();

                    System.out.print("Enter product name: ");
                    String productName = scannerStr.nextLine();
                    System.out.print("Enter product amount: ");
                    int productAmount = scannerInt.nextInt();

                    String res = productService.addProductAmount(productName, productAmount);
                    System.out.println(res);
                }
                case 6 -> {
                    getProductsBycategoryId();
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

    private static void listOfProducts() {
        List<Product> products = productService.getProducts();
        if (products.isEmpty()) {
            System.out.println("No product yet!");
        } else {
            System.out.println("Products list:");
            System.out.println("------------------");
            for (Product product : products) {
                Category category = categoryService.getCategoryById(product.getCategoryId());
                System.out.printf("""
                        Name: %s
                        Price: %S
                        Amount: %d
                        Category: %s
                        ------------------
                        \n""", product.getName(), product.getPrice(), product.getAmount(), (category != null ? category.getCatName() : "Head"));
            }
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
        if (choice == 1) {
            List<Category> categories = categoryService.getChildCategories(UUID.fromString(id));
            if (categories.isEmpty()) {
                System.out.println("This category has no sub category :(");
            } else {
                System.out.println("Categories: ");
                for (Category category : categories) {
                    System.out.println(category);
                }
            }
        } else if (choice == 2) {
            List<Category> categories = categoryService.getCategories();
            Set<UUID> AllCategories = categoryService.getSubCategories(UUID.fromString(id));
            for (Category category : categories) {
                if (AllCategories.contains(category.getCatId())) {
                    System.out.println(category);
                }
            }


        } else {
            System.out.println("Invalid comment");
        }
    }

    private static void deleteProduct(User currUser) {
        List<Product> products = productService.getProducts();
        if (products.isEmpty()) {
            System.out.println("No product yet!");
        } else {
            System.out.println("Products list:");
            System.out.println("-----------------");
            for (Product product : products) {
                Category category = categoryService.getCategoryById(product.getCategoryId());
                System.out.printf("""
                        Name: %s
                        Price: %S
                        Amount: %d
                        Category: %s
                        ------------------
                        \n""", product.getName(), product.getPrice(), product.getAmount(), (category != null ? category.getCatName() : "Head"));
            }
        }
        System.out.print("Enter product name: ");
        boolean res = productService.deleteProductByName(scannerStr.nextLine());
        System.out.println("Product is " + (res ? "deteled!" : "not found"));

    }

    private static void listOfCategories() {
        List<Category> cats = categoryService.getCategories();
        if (cats.isEmpty()) {
            System.out.println("Here is not category yet!");
        } else {
            System.out.println("Gategories: ");
            for (Category cat : cats) {
                System.out.println(cat);
            }
        }
    }

    private static void getProductsBycategoryId() {
        UUID catId = chooseParentCategory();
        Set<UUID> catsId = categoryService.getSubCategories(catId);
        Category category = categoryService.getCategoryById(catId);
        List<Product> products = productService.getProductsByCategory(catsId);

        if (products.isEmpty()) {
            System.out.printf("%s category is empty\n", (category != null ? category.getCatName() : "Head"));
        } else {
            System.out.println("Products list");
            System.out.println("------------------");
            for (Product product : products) {
                Category productCat = categoryService.getCategoryById(product.getCategoryId());
                System.out.printf("""
                        Name: %s
                        Price: %S
                        Amount: %d
                        Category: %s
                        ------------------
                        \n""", product.getName(), product.getPrice(), product.getAmount(), (productCat != null ? productCat.getCatName() : "Head"));
            }
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

        boolean res = productService.addProduct(new Product(name, price, amount, catId));
        System.out.println("Product is " + (res ? "added!" : "not added!"));
    }

    private static void addCategory(User currUser) {
        UUID parentId = chooseParentCategory();

        System.out.print("Enter new category name: ");
        String catName = scannerStr.nextLine();

        boolean added = categoryService.addCategory(catName, parentId);
        System.out.println("Category " + (added ? "added!" : "failed (duplicate?)"));
    }

    private static UUID chooseParentCategory() {
        UUID currId = null;

        while (true) {
            UUID finalId = currId;
            List<Category> categories = categoryService.getCategories();
            List<Category> children = new ArrayList<>();

            for (Category category : categories) {
                if (Objects.equals(category.getParentId(), finalId)) {
                    children.add(category);
                }
            }

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
            } else {
                System.out.println("This category is empty, please press 0!");
            }

            System.out.print("Select category: ");
            int res = scannerInt.nextInt();

            if (res == 0) return currId;

            int idx = res - 1;
            if (idx >= 0 && idx < children.size()) {
                currId = children.get(idx).getCatId();
            } else {
                System.out.println("Invalid command!");
            }
        }
    }

    private static void userMenu(User currUser) {
        int stepCode = 10;
        while (stepCode != 0) {
            System.out.println("""
                    1. Create card
                    2. List of cards
                    3. Exit
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
                    stepCode = 0;
                }
                default -> {
                    System.out.println("Something went wrong");
                }
            }
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
                            No %d | %s
                            price: %s
                            remaining: %d
                            """, c++, product.getName(), product.getPrice(), product.getAmount());
                }
                System.out.print("Choose product (enter product name): ");
                String name = scannerStr.nextLine();

                System.out.print("How many or How Much do you want: ");
                int quantity = scannerInt.nextInt();

                Product seletctedProduct = productService.getProductByName(name, quantity);
                //System.out.println(seletctedProduct);


                if (seletctedProduct != null) {
                    newCard.getOrders().add(new Order(seletctedProduct, quantity));
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