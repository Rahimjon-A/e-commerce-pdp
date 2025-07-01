package com.shop.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@JacksonXmlRootElement(localName = "Category")
@ToString
@Data
public class Category {
    private UUID catId;
    private String catName;
    private String createdBy;
    private Date createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID parentId;

    public Category() {
        this.catId = UUID.randomUUID();
    }

    public Category(String catName, String createdBy, Date createdAt, UUID parentId) {
        this();
        this.catName = catName;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.parentId = parentId;
    }

}
