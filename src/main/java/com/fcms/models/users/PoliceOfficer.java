package com.fcms.models.users;

public class PoliceOfficer extends UserAccount {

    private String rank;
    private String department;

    public PoliceOfficer(String userID, String username, String name, String email,
                         String password, String role, String managedBy,
                         boolean approved, String createdAt,
                         String rank, String department) {

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

        this.rank = rank;
        this.department = department;
    }

    public String getRank() { return rank; }
    public String getDepartment() { return department; }
}
