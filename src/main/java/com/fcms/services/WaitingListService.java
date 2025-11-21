package com.fcms.services;

import com.fcms.models.PendingUser;

import java.util.ArrayList;
import java.util.List;

public class WaitingListService {

    private final List<PendingUser> pendingUsers = new ArrayList<>();

    public WaitingListService() {
        loadDummyData();
    }

    // Business Logic -----------------------------------------

    public List<PendingUser> getPendingUsers() {
        return new ArrayList<>(pendingUsers);
    }

    public void approve(PendingUser user) {
        pendingUsers.remove(user);
    }

    public void reject(PendingUser user) {
        pendingUsers.remove(user);
    }

    // Dummy data for now -------------------------------------
    private void loadDummyData() {
        pendingUsers.add(new PendingUser(
                "John Smith", "Police Officer",
                "john.smith@casevault.gov", "XYZ County",
                "Requesting access for criminal investigation duties",
                "1/15/2024"
        ));

        pendingUsers.add(new PendingUser(
                "Jane Doe", "Forensic Expert",
                "jane.doe@casevault.gov", "ABC County",
                "Need access to forensic evidence database",
                "11/19/2024"
        ));

        pendingUsers.add(new PendingUser(
                "Adam Ray", "Court Official",
                "adam.ray@casevault.gov", "Central Court",
                "Managing pending case verdicts",
                "9/10/2024"
        ));
    }
}
