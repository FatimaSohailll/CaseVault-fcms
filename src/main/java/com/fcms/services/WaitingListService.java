package com.fcms.services;

import com.fcms.models.users.UserAccount;
import com.fcms.repositories.UserRepository;

import java.util.List;

public class WaitingListService {

    public List<UserAccount> getPendingUsers() {
        return UserRepository.getPendingUsers();
    }

    public void approve(UserAccount user) {

        // Set approved=true, managedBy=System Admin
        UserRepository.updateStatus(user.getUserID(), true, "System Admin");

        UserRepository.insertHistory("System Admin",
                "Approved pending user: " + user.getName());
    }

    public void reject(UserAccount user) {

        UserRepository.deleteUser(user.getUserID());

        UserRepository.insertHistory("System Admin",
                "Rejected and removed user: " + user.getName());
    }

}
