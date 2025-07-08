package com.shop.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shop.enums.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@JacksonXmlRootElement(localName = "Users")
@Getter
@ToString
public class User {
    private UUID userId;
    @Setter
    private String fullName;
    @Setter
    private String userName;
    @Setter
    private String phoneNumber;
    @Setter
    private String password;
    @Setter
    private Role role;
    @Setter
    private UUID cardId;

    public User() {
        this.userId = UUID.randomUUID();
        this.role = Role.USER;
    }

    public User(String fullName, String userName, String phoneNumber, String password) {
        this();
        this.fullName = fullName;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
