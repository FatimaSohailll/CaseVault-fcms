package com.fcms.models.users;

public class CourtOfficial extends UserAccount {

    private String courtName;
    private String designation;

    public CourtOfficial(String userID, String username, String name, String email,
                         String password, String role, String managedBy,
                         boolean approved, String createdAt,
                         String courtName, String designation) {

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

        this.courtName = courtName;
        this.designation = designation;
    }

    public String getCourtName() { return courtName; }
    public String getDesignation() { return designation; }
}
