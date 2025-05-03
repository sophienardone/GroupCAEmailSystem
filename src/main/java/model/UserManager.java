package model;

import utils.SecurityUtils;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, User> users;

    public UserManager() {
        this.users = new HashMap<>();
    }

    public boolean registerUser(String username, String rawPassword) {
        if (users.containsKey(username)) return false;
        String hashedPassword = SecurityUtils.hashPassword(rawPassword);
        users.put(username, new User(username, hashedPassword));
        return true;
    }

    public boolean authenticate(String username, String rawPassword) {
        User user = users.get(username);
        if (user == null) return false;
        String hashedPassword = SecurityUtils.hashPassword(rawPassword);
        return user.getPassword().equals(hashedPassword);
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    // login with socket
    public boolean login(String username, String password, Socket clientSocket) {
        return authenticate(username, password);
    }

}
