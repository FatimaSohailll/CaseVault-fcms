package com.fcms.services;

import com.fcms.models.Evidence;
import com.fcms.models.Case;
import com.fcms.repositories.EvidenceRepository;
import com.fcms.repositories.ChainofCustodyRepository;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class EvidenceService {
    private EvidenceRepository evidenceRepository;
    private ChainofCustodyRepository chainofCustodyRepository;

    // Updated constructor to accept case ID
    public EvidenceService(String caseId) {
        this.evidenceRepository = new EvidenceRepository(caseId);
        this.chainofCustodyRepository = new ChainofCustodyRepository();
    }

    // Alternative constructor for cases where no specific case is selected
    public EvidenceService() {
        this.evidenceRepository = new EvidenceRepository();
    }

    // Business logic for adding evidence
    public void addEvidence(Evidence evidence) throws BusinessException {
        // Business validation
        if (evidence.getType() == null || evidence.getType().trim().isEmpty()) {
            throw new BusinessException("Evidence type is required");
        }
        if (evidence.getDescription() == null || evidence.getDescription().trim().isEmpty()) {
            throw new BusinessException("Evidence description is required");
        }
        if (evidence.getLocation() == null || evidence.getLocation().trim().isEmpty()) {
            throw new BusinessException("Collection location is required");
        }
        if (evidence.getCollectionDateTime() == null) {
            throw new BusinessException("Collection date and time are required");
        }

        // Business rule: Generate evidence ID if not provided
        if (evidence.getId() == null || evidence.getId().trim().isEmpty()) {
            evidence.setId(generateEvidenceId());
        }

        // Delegate to repository
        evidenceRepository.save(evidence);
    }

    // Business logic for adding evidence with file
    public void addEvidenceWithFile(Evidence evidence, File file) throws BusinessException {
        // Business validation for file
        if (file != null) {
            // Business rule: File size limit (50MB)
            if (file.length() > 50 * 1024 * 1024) {
                throw new BusinessException("File size must be less than 50MB");
            }

            // Business rule: Validate file type
            if (!isValidFileType(file)) {
                throw new BusinessException("Invalid file type. Allowed types: PDF, JPG, PNG, MP4, AVI, MOV");
            }

            evidence.setFileName(file.getName());
            //evidence.setFile(file);
        }

        addEvidence(evidence);
    }

    // Business logic for updating evidence
    public void updateEvidence(Evidence evidence) throws BusinessException {
        // Business validation
        if (evidence.getId() == null || evidence.getId().trim().isEmpty()) {
            throw new BusinessException("Evidence ID is required for update");
        }
        if (evidence.getType() == null || evidence.getType().trim().isEmpty()) {
            throw new BusinessException("Evidence type is required");
        }
        if (evidence.getDescription() == null || evidence.getDescription().trim().isEmpty()) {
            throw new BusinessException("Evidence description is required");
        }
        if (evidence.getLocation() == null || evidence.getLocation().trim().isEmpty()) {
            throw new BusinessException("Collection location is required");
        }
        if (evidence.getCollectionDateTime() == null) {
            throw new BusinessException("Collection date and time are required");
        }

        // Check if evidence exists
        Evidence existingEvidence = evidenceRepository.findById(evidence.getId());
        if (existingEvidence == null) {
            throw new BusinessException("Evidence not found with ID: " + evidence.getId());
        }

        // Delegate to repository
        evidenceRepository.update(evidence);
    }

    // Business logic for updating evidence with file
    public void updateEvidenceWithFile(Evidence evidence, File file) throws BusinessException {
        // Business validation for file
        if (file != null) {
            // Business rule: File size limit (50MB)
            if (file.length() > 50 * 1024 * 1024) {
                throw new BusinessException("File size must be less than 50MB");
            }

            // Business rule: Validate file type
            if (!isValidFileType(file)) {
                throw new BusinessException("Invalid file type. Allowed types: PDF, JPG, PNG, MP4, AVI, MOV");
            }

            evidence.setFileName(file.getName());
            //evidence.setFile(file);
        }

        updateEvidence(evidence);
    }

    // Business logic for deleting evidence
    public void deleteEvidence(String evidenceId) throws BusinessException {
        if (evidenceId == null || evidenceId.trim().isEmpty()) {
            throw new BusinessException("Evidence ID is required for deletion");
        }

        // Check if evidence exists
        Evidence existingEvidence = evidenceRepository.findById(evidenceId);
        if (existingEvidence == null) {
            throw new BusinessException("Evidence not found with ID: " + evidenceId);
        }

        // Additional business rules before deletion
        // For example: Check if evidence is linked to any forensic requests
        // if (isEvidenceInUse(evidenceId)) {
        //     throw new BusinessException("Cannot delete evidence that is linked to forensic requests");
        // }

        evidenceRepository.delete(evidenceId);
    }

    // Business logic for retrieving evidence by ID
    public Evidence getEvidenceById(String evidenceId) throws BusinessException {
        if (evidenceId == null || evidenceId.trim().isEmpty()) {
            throw new BusinessException("Evidence ID is required");
        }

        Evidence evidence = evidenceRepository.findById(evidenceId);
        if (evidence == null) {
            throw new BusinessException("Evidence not found with ID: " + evidenceId);
        }

        return evidence;
    }

    // Business logic for retrieving all evidence
    public List<Evidence> getAllEvidence() {
        return evidenceRepository.findAll();
    }

    // Business logic for getting evidence by case
    public List<Evidence> getEvidenceByCase(String caseId) {
        return evidenceRepository.findByCaseId(caseId);
    }

    // Business logic for checking if evidence exists for a case
    public boolean hasEvidenceForCase(String caseId) {
        List<Evidence> evidenceList = getEvidenceByCase(caseId);
        return evidenceList != null && !evidenceList.isEmpty();
    }

    // Business logic for getting evidence count by case
    public int getEvidenceCountByCase(String caseId) {
        List<Evidence> evidenceList = getEvidenceByCase(caseId);
        return evidenceList != null ? evidenceList.size() : 0;
    }

    // Business rule: Validate file type
    private boolean isValidFileType(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".pdf") || fileName.endsWith(".jpg") ||
                fileName.endsWith(".jpeg") || fileName.endsWith(".png") ||
                fileName.endsWith(".mp4") || fileName.endsWith(".avi") ||
                fileName.endsWith(".mov") || fileName.endsWith(".doc") ||
                fileName.endsWith(".docx") || fileName.endsWith(".txt");
    }

    // Business rule: Generate evidence ID
    private String generateEvidenceId() {
        List<Evidence> evidenceList = evidenceRepository.findAll();
        int nextId = evidenceList.size() + 1;
        return "EV" + String.format("%04d", nextId);
    }

    // Business rule: Validate evidence can be modified
    public boolean canModifyEvidence(String evidenceId) throws BusinessException {
        Evidence evidence = getEvidenceById(evidenceId);
        if (evidence == null) {
            return false;
        }

        // Add business rules for modification
        // For example: Evidence cannot be modified if it's part of a closed case
        // or if forensic analysis is in progress

        return true;
    }

    // Business rule: Check if evidence is linked to any active forensic requests
    public boolean isEvidenceInUse(String evidenceId) {
        // Implementation would depend on your forensic request service
        // This is a placeholder for the actual business logic
        return false;
    }

    // Business logic for bulk operations
    public void addMultipleEvidence(List<Evidence> evidenceList) throws BusinessException {
        for (Evidence evidence : evidenceList) {
            addEvidence(evidence);
        }
    }

    // Business logic for evidence search
    public List<Evidence> searchEvidence(String searchTerm) {
        return evidenceRepository.search(searchTerm);
    }

    // Business logic for evidence filtering
    public List<Evidence> filterEvidenceByType(String evidenceType) {
        return evidenceRepository.findByType(evidenceType);
    }

    // Business logic for evidence date range filtering
    public List<Evidence> filterEvidenceByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return evidenceRepository.findByDateRange(startDate, endDate);
    }
}