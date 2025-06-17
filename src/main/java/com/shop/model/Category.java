package com.shop.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@JacksonXmlRootElement(localName = "Category")
@ToString
@Data
public class Category {
    private UUID catId;
    private String catName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID parentId;

    public Category() {
        this.catId = UUID.randomUUID();
    }

    public Category(String catName, UUID parentId) {
        this();
        this.catName = catName;
        this.parentId = parentId;
    }
}
