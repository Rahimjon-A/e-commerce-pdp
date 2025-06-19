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

    public  CategoryService(ProductService productService) {
        this.productService = productService;
        CategoryWrapper wrapper = FileUtility.loadFileFromXML(CATEGORY_FILE, CategoryWrapper.class);
        categories = wrapper != null ? wrapper.getCategories() : new ArrayList<>();
    }


    public boolean addCategory(String name, UUID parentId) {
        for (Category category : categories) {
            if(category.getCatName().equals(name) && Objects.equals(category.getParentId(), parentId)) {
                return false;
            }
        }

        categories.add(new Category(name, parentId));
        FileUtility.saveFileToXML(CATEGORY_FILE, new CategoryWrapper(categories));
        return true;
    }


    public Category getCategoryById(UUID id) {
        for (Category category : categories) {
            if(Objects.equals(category.getCatId(), id)) {
                return category;
            }
        }
        return null;
    }

    public boolean deleteCategory(UUID id) {
        if(id == null) return false;

        Set<UUID> catsToDel = getSubCategories(id);

        categories.removeIf(category -> catsToDel.contains(category.getCatId()));
        productService.getProducts().removeIf(product -> catsToDel.contains(product.getCategoryId()));
        productService.update();
        FileUtility.saveFileToXML(CATEGORY_FILE, new CategoryWrapper(categories));
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
            if(Objects.equals(id, category.getParentId())) {
                getSubCategories(category.getCatId(), catsToDel);
            }
        }
    }

    public List<Category> getChildCategories(UUID id) {
        List<Category> children = new ArrayList<>();
        for (Category category : categories) {
            if(Objects.equals(category.getParentId(), id)) {
                children.add(category);
            }
        }
        return children;
    }

}
