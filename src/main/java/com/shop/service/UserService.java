package com.shop.service;

import com.shop.model.User;
import com.shop.model.wrapper.UserWrapper;
import com.shop.utility.FileUtility;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public class UserService {
    private static final String USER_FILE = "./files/users.xml";


    private final List<User> users;

    public UserService() {
        UserWrapper wrapper = FileUtility.loadFileFromXML(USER_FILE, UserWrapper.class);
        this.users = wrapper != null ? wrapper.getUsers() : new ArrayList<>();
    }

    public boolean register(User user) {
        if (getUserByUserName(user.getUserName()) != null) {
            return false;
        }
        users.add(user);
        this.update();
        return true;
    }

    public User login(String userName, String password) {
        User user = getUserByUserName(userName);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }


    public User getUserByUserName(String userName) {
        return users.stream()
                .filter(user -> user.getUserName().equals(userName))
                .findFirst()
                .orElse(null);
    }

    public void update() {
        FileUtility.saveFileToXML(USER_FILE, new UserWrapper(users));
    }
}
