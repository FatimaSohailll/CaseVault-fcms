package com.fcms.repositories;

import com.fcms.models.Case;

import java.util.ArrayList;
import java.util.List;

public class CaseRepository {
    private List<Case> cases;

    public CaseRepository() {
        this.cases = new ArrayList<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        cases.add(new Case("CASE-001", "Armed Robbery - Downtown Bank", "Robbery",
                "Officer J. Smith", "123 Main Street, Downtown",
                java.time.LocalDate.of(2024, 3, 15), "Under Investigation"));
        cases.add(new Case("CASE-002", "Residential Burglary - Oak Street", "Burglary",
                "Officer R. Khan", "17 Oak Street",
                java.time.LocalDate.of(2024, 3, 18), "Evidence Analysis"));
        cases.add(new Case("CASE-003", "Vehicle Theft - Shopping Mall", "Theft",
                "Officer S. Hussain", "Mall Parking Lot C",
                java.time.LocalDate.of(2024, 3, 18), "Under Investigation"));
    }

    // Save new case
    public void save(Case c) {
        cases.add(c);
        System.out.println("Saved case: " + c.getId());
    }

    // Update existing case
    public void update(Case c) {
        for (int i = 0; i < cases.size(); i++) {
            if (cases.get(i).getId().equals(c.getId())) {
                cases.set(i, c);
                System.out.println("Updated case: " + c.getId());
                return;
            }
        }
        throw new RuntimeException("Case not found: " + c.getId());
    }

    // Find all cases
    public List<Case> findAll() {
        return new ArrayList<>(cases);
    }

    // Find case by ID
    public Case findById(String id) {
        return cases.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Check if case exists
    public boolean exists(String id) {
        return cases.stream().anyMatch(c -> c.getId().equals(id));
    }

    // Delete case
    public void delete(String id) {
        cases.removeIf(c -> c.getId().equals(id));
        System.out.println("Deleted case: " + id);
    }
}