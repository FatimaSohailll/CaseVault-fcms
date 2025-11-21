package com.fcms.repositories;

import com.fcms.models.Evidence;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EvidenceRepository {
    private List<Evidence> evidenceList;

    public EvidenceRepository() {
        this.evidenceList = new ArrayList<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        evidenceList.add(new Evidence("EV-001", "Security camera footage from bank entrance", "Physical"));
        evidenceList.add(new Evidence("EV-002", "Transaction records and access logs", "Digital"));
        evidenceList.add(new Evidence("EV-003", "Fingerprints lifted from door handle", "Physical"));
    }

    public void save(Evidence evidence) {
        evidenceList.add(evidence);
        System.out.println("Saved evidence: " + evidence.getId());
    }

    public List<Evidence> findAll() {
        return new ArrayList<>(evidenceList);
    }

    public List<Evidence> findByCaseId(String caseId) {
        return evidenceList.stream()
                .filter(e -> caseId.equals(e.getCaseId()))
                .collect(Collectors.toList());
    }

    public Evidence findById(String id) {
        return evidenceList.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}