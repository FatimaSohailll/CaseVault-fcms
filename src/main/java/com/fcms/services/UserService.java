package com.fcms.services;

import com.fcms.repositories.UserRepository;
import com.fcms.models.users.UserAccount;

import java.util.List;

public class UserService {

    // Get all users
    public List<UserAccount> getAllUsers() {
        return UserRepository.getAllUsers();
    }

    // Delete user
    public void deleteUser(String userID) {
        UserRepository.deleteUser(userID);
    }

    // Add user
    public void addUser(UserAccount user) {
        UserRepository.insertUser(user);
    }

    // Update user
    public void updateUser(UserAccount user) {
        UserRepository.updateUser(user);
    }

    // Count users
    public int getUserCount() {
        return UserRepository.getUserCount();
    }
}
