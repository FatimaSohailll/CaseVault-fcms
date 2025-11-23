package com.fcms.models.users;

public class ForensicExpert extends UserAccount {

    private String labName;

    public ForensicExpert(String userID, String username, String name, String email,
                          String password, String role, String managedBy,
                          boolean approved, String createdAt,
                          String labName) {

        super(
                userID,
                username,
                name,
                email,
                password,
                role,
                managedBy,
                approved,
                createdAt
        );

        this.labName = labName;
    }

    public String getLabName() { return labName; }
}
