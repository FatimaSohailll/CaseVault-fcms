package com.fcms.services;

import com.fcms.models.Participant;
import com.fcms.repositories.ParticipantRepository;
import java.util.List;

public class ParticipantService {
    private ParticipantRepository participantRepository;

    public ParticipantService() {
        this.participantRepository = new ParticipantRepository();
    }

    // Business logic for adding a participant
    public void addParticipant(Participant participant) throws BusinessException {
        // Business validation
        if (participant.getName() == null || participant.getName().trim().isEmpty()) {
            throw new BusinessException("Participant name is required");
        }
        if (participant.getRole() == null || participant.getRole().trim().isEmpty()) {
            throw new BusinessException("Participant role is required");
        }
        if (participant.getContact() == null || participant.getContact().trim().isEmpty()) {
            throw new BusinessException("Contact information is required");
        }

        // Business rule: Check for duplicates
        if (isDuplicateParticipant(participant)) {
            throw new BusinessException("Participant with same name and role already exists");
        }

        // Business rule: Generate ID
        participant.setId(generateParticipantId());

        // Delegate to repository
        participantRepository.save(participant);
    }

    // Business logic for updating a participant
    public void updateParticipant(Participant participant) throws BusinessException {
        // Business validation
        if (participant.getId() == null) {
            throw new BusinessException("Participant ID is required for update");
        }

        // Check if participant exists
        if (!participantRepository.exists(participant.getId())) {
            throw new BusinessException("Participant not found");
        }

        // Delegate to repository
        participantRepository.update(participant);
    }

    // Business logic for retrieving all participants
    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    // Business logic for finding participant by ID
    public Participant getParticipantById(String id) throws BusinessException {
        Participant participant = participantRepository.findById(id);
        if (participant == null) {
            throw new BusinessException("Participant not found with ID: " + id);
        }
        return participant;
    }

    // Business rule: Check for duplicate participants
    private boolean isDuplicateParticipant(Participant participant) {
        List<Participant> existingParticipants = participantRepository.findAll();
        return existingParticipants.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(participant.getName())
                        && p.getRole().equals(participant.getRole()));
    }

    // Business rule: Generate participant ID
    private String generateParticipantId() {
        List<Participant> participants = participantRepository.findAll();
        int nextId = participants.size() + 1;
        return "P-" + String.format("%03d", nextId);
    }
}