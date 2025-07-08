package com.shop.service;

import com.shop.model.Category;
import com.shop.model.wrapper.CategoryWrapper;
import com.shop.utility.FileUtility;
import lombok.Getter;

import java.util.*;

@Getter
public class CategoryService {
    private static final String CATEGORY_FILE = "./files/category.xml";
    private final List<Category> categories;
    private final ProductService productService;

    public CategoryService() {
        this.productService = new ProductService();
        CategoryWrapper wrapper = FileUtility.loadFileFromXML(CATEGORY_FILE, CategoryWrapper.class);
        categories = wrapper != null ? wrapper.getCategories() : new ArrayList<>();
    }


    public boolean addCategory(Category newCategory) {

        boolean b = categories.stream()
                .anyMatch(category ->
                        category.getCatName().equals(newCategory.getCatName()) && Objects.equals(category.getParentId(), newCategory.getCatId()));

        if (b) return false;

        categories.add(newCategory);
        this.update();
        return true;
    }

    public Category getCategoryById(UUID id) {
        return categories.stream()
                .filter(category -> Objects.equals(category.getCatId(), id))
                .findFirst()
                .orElse(null);
    }

    public boolean deleteCategory(UUID id) {
        if (id == null) return false;

        Set<UUID> catsToDel = getSubCategories(id);

        categories.removeIf(category -> catsToDel.contains(category.getCatId()));
        productService.deleteCategoryProducts(catsToDel);

        this.update();
        return true;
    }

    public Set<UUID> getSubCategories(UUID id) {
        Set<UUID> set = new HashSet<>();
        getSubCategories(id, set);
        return set;
    }

    private void getSubCategories(UUID id, Set<UUID> catsToDel) {
        catsToDel.add(id);
        for (Category category : categories) {
            if (Objects.equals(id, category.getParentId())) {
                getSubCategories(category.getCatId(), catsToDel);
            }
        }
    }

    public List<Category> getChildCategories(UUID id) {
        return categories.stream()
                .filter(category -> Objects.equals(category.getParentId(), id))
                .toList();
    }

    public UUID getParentId(UUID categoryId) {
        Category category = getCategoryById(categoryId); // implement or reuse
        return category != null ? category.getParentId() : null;
    }


    public void update() {
        FileUtility.saveFileToXML(CATEGORY_FILE, new CategoryWrapper(categories));
    }
}