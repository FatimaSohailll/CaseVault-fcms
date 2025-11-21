package com.fcms.repositories;

import com.fcms.models.Participant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipantRepository {
    private List<Participant> participants; // In-memory storage, replace with database

    public ParticipantRepository() {
        this.participants = new ArrayList<>();
        // Initialize with sample data
        initializeSampleData();
    }

    private void initializeSampleData() {
        participants.add(new Participant("P-001", "John Anderson", "Suspect", "555-0123", "Driver License", "DL-8765432"));
        participants.add(new Participant("P-002", "Sarah Mitchell", "Victim", "555-0124", "Passport", "PS-1234567"));
        participants.add(new Participant("P-003", "Michael Chen", "Witness", "555-0125", "State ID", "ID-9876543"));
    }

    public void save(Participant participant) {
        participants.add(participant);
        // In real application: Save to database
        System.out.println("Saved participant: " + participant.getId());
    }

    public void update(Participant participant) {
        // Find and update existing participant
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getId().equals(participant.getId())) {
                participants.set(i, participant);
                System.out.println("Updated participant: " + participant.getId());
                return;
            }
        }
        throw new RuntimeException("Participant not found: " + participant.getId());
    }

    public List<Participant> findAll() {
        return new ArrayList<>(participants);
    }

    public Participant findById(String id) {
        return participants.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean exists(String id) {
        return participants.stream().anyMatch(p -> p.getId().equals(id));
    }
}