package com.fcms.services;

import com.fcms.models.users.UserAccount;
import com.fcms.repositories.UserRepository;

import java.util.List;

public class WaitingListService {

    public List<UserAccount> getPendingUsers() {
        return UserRepository.getPendingUsers();
    }

    public void approve(UserAccount user) {
        // Update status → user is now active
        UserRepository.updateStatus(user.getUserID(), "System Admin");

        // Add history entry
        UserRepository.addHistory("System Admin",
                "Approved pending user: " + user.getName());
    }

    public void reject(UserAccount user) {
        // Delete user from all related tables (CASCADE works)
        UserRepository.deleteUser(user.getUserID());

        // Add history entry
        UserRepository.addHistory("System Admin",
                "Rejected and removed user: " + user.getName());
    }
}
