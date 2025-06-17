package com.shop.service;

import com.shop.model.User;
import com.shop.model.wrapper.UserWrapper;
import com.shop.utility.FileUtility;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
        FileUtility.saveFileToXML(USER_FILE, new UserWrapper(users));
        return true;
    }

    public User login(String userName, String password) {
        User user = getUserByUserName(userName);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }


    public void update() {
        FileUtility.saveFileToXML(USER_FILE, new UserWrapper(users));
    }

    public User getUserByUserName(String userName) {
        for (User user : users) {
            if(user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null;
    }
}
