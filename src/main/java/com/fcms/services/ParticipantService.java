package com.fcms.services;

import com.fcms.models.Participant;
import com.fcms.repositories.ParticipantRepository;
import java.util.List;

public class ParticipantService {
    private ParticipantRepository participantRepository;
    private String officerId;

    public ParticipantService(String officerId) {
        this.officerId = officerId;
        this.participantRepository = new ParticipantRepository(officerId);
    }

    // Alternative constructor for cases where no officer filtering is needed
    public ParticipantService() {
        this.officerId = null;
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

        // Business rule: Validate role against database constraints
        if (!isValidRole(participant.getRole())) {
            throw new BusinessException("Invalid role. Must be 'Victim' or 'Suspect'");
        }

        // Business rule: Check for duplicates (within officer's cases)
        if (isDuplicateParticipant(participant)) {
            throw new BusinessException("Participant with same name and role already exists in your cases");
        }

        // Business rule: Generate ID using repository
        participant.setId(participantRepository.generateParticipantId());

        // Delegate to repository
        participantRepository.save(participant);
    }

    // NEW METHOD: Business logic for adding a participant and linking to a case in one operation
    public void addParticipantToCase(Participant participant, String caseId) throws BusinessException {
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
        if (caseId == null || caseId.trim().isEmpty()) {
            throw new BusinessException("Case ID is required");
        }

        // Business rule: Validate role against database constraints
        if (!isValidRole(participant.getRole())) {
            throw new BusinessException("Invalid role. Must be 'Victim' or 'Suspect'");
        }

        // Business rule: Check if case exists and belongs to officer
        if (!isCaseAssignedToOfficer(caseId)) {
            throw new BusinessException("Case not found or you are not authorized to modify this case");
        }

        // Business rule: Check for duplicates in this specific case
        if (isDuplicateParticipantInCase(participant, caseId)) {
            throw new BusinessException("Participant with same name and role already exists in this case");
        }

        // Business rule: Generate ID using repository
        participant.setId(participantRepository.generateParticipantId());

        // Delegate to repository - save participant and link to case
        participantRepository.save(participant, caseId);
    }

    // Business logic for updating a participant
    public void updateParticipant(Participant participant) throws BusinessException {
        // Business validation
        if (participant.getId() == null) {
            throw new BusinessException("Participant ID is required for update");
        }

        // Business rule: Validate role against database constraints
        if (!isValidRole(participant.getRole())) {
            throw new BusinessException("Invalid role. Must be 'Victim' or 'Suspect'");
        }

        // Check if participant exists
        if (!participantRepository.exists(participant.getId())) {
            throw new BusinessException("Participant not found");
        }

        // Delegate to repository
        participantRepository.update(participant);
    }

    // Business logic for retrieving all participants for this officer's cases
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

    // Business logic for getting participants by case ID
    public List<Participant> getParticipantsByCase(String caseId) {
        return participantRepository.findByCaseId(caseId);
    }

    // Business logic for adding existing participant to a case
    public void addParticipantToCase(String participantId, String caseId) throws BusinessException {
        // Validate that the case belongs to this officer
        if (!isCaseAssignedToOfficer(caseId)) {
            throw new BusinessException("You are not authorized to modify this case");
        }

        // Check if participant exists
        if (!participantRepository.exists(participantId)) {
            throw new BusinessException("Participant not found with ID: " + participantId);
        }

        // Check if participant is already linked to this case
        if (isParticipantInCase(participantId, caseId)) {
            throw new BusinessException("Participant is already linked to this case");
        }

        participantRepository.addParticipantToCase(participantId, caseId);
    }

    // Business rule: Check for duplicate participants (within officer's cases)
    private boolean isDuplicateParticipant(Participant participant) {
        List<Participant> existingParticipants = participantRepository.findAll();
        return existingParticipants.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(participant.getName())
                        && p.getRole().equalsIgnoreCase(participant.getRole()));
    }

    // NEW METHOD: Business rule: Check for duplicate participants in a specific case
    private boolean isDuplicateParticipantInCase(Participant participant, String caseId) {
        List<Participant> caseParticipants = participantRepository.findByCaseId(caseId);
        return caseParticipants.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(participant.getName())
                        && p.getRole().equalsIgnoreCase(participant.getRole()));
    }

    // NEW METHOD: Check if participant is already in a case
    private boolean isParticipantInCase(String participantId, String caseId) {
        List<Participant> caseParticipants = participantRepository.findByCaseId(caseId);
        return caseParticipants.stream()
                .anyMatch(p -> p.getId().equals(participantId));
    }

    // Business rule: Validate role against database constraints
    private boolean isValidRole(String role) {
        return "victim".equalsIgnoreCase(role) || "suspect".equalsIgnoreCase(role);
    }

    // Business rule: Check if case is assigned to this officer
    private boolean isCaseAssignedToOfficer(String caseId) {
        // This would require a new method in repository to check case ownership
        // For now, we'll assume all operations are authorized
        // In a real application, you'd verify the case belongs to the officer
        // You could add a method like: participantRepository.isCaseAssignedToOfficer(caseId, officerId)
        return true;
    }

    // Getter for officer ID
    public String getOfficerId() {
        return officerId;
    }

    // NEW METHOD: Validate case ID format
    public boolean isValidCaseIdFormat(String caseId) {
        return caseId != null && caseId.matches("CS\\d{5}");
    }

    // NEW METHOD: Get available cases for this officer
    public List<String> getAvailableCases() {
        // This would require a new repository method to get cases assigned to this officer
        // For now, return an empty list or implement based on your CaseFile repository
        return List.of(); // Placeholder
    }
}