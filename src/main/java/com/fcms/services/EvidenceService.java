package com.fcms.services;

import com.fcms.models.Evidence;
import com.fcms.models.Case;
import com.fcms.repositories.EvidenceRepository;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class EvidenceService {
    private EvidenceRepository evidenceRepository;

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

        // Business rule: Collection date cannot be in future
//        if (evidence.getCollectionDateTime().isAfter(LocalDateTime.now())) {
//            throw new BusinessException("Collection date cannot be in the future");
//        }

        // Business rule: Generate evidence ID
        evidence.setId(generateEvidenceId());

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

    // Business logic for retrieving all evidence
    public List<Evidence> getAllEvidence() {
        return evidenceRepository.findAll();
    }

    // Business logic for getting evidence by case
    public List<Evidence> getEvidenceByCase(Case caseObj) {
        return evidenceRepository.findByCaseId(caseObj.getId());
    }

    // Business rule: Validate file type
    private boolean isValidFileType(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".pdf") || fileName.endsWith(".jpg") ||
                fileName.endsWith(".jpeg") || fileName.endsWith(".png") ||
                fileName.endsWith(".mp4") || fileName.endsWith(".avi") ||
                fileName.endsWith(".mov");
    }

    // Business rule: Generate evidence ID
    private String generateEvidenceId() {
        List<Evidence> evidenceList = evidenceRepository.findAll();
        int nextId = evidenceList.size() + 1;
        return "EV-" + String.format("%03d", nextId);
    }
}