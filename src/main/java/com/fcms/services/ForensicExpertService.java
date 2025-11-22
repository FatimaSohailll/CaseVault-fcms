package com.fcms.services;

import com.fcms.models.ForensicExpert;
import com.fcms.repositories.ForensicExpertRepository;
import java.util.List;

public class ForensicExpertService {
    private ForensicExpertRepository expertRepository;

    public ForensicExpertService() {
        this.expertRepository = new ForensicExpertRepository();
    }

    // Business logic for getting all forensic experts
    public List<ForensicExpert> getAllExperts() {
            return expertRepository.findAll();
    }

    // Business logic for getting expert by ID
    public ForensicExpert getExpertById(String expertId) throws BusinessException {
        if (expertId == null || expertId.trim().isEmpty()) {
            throw new BusinessException("Expert ID is required");
        }

        ForensicExpert expert = expertRepository.findById(expertId);
        if (expert == null) {
            throw new BusinessException("Forensic expert not found with ID: " + expertId);
        }
        return expert;
    }

    // Business logic for checking if expert exists
    public boolean expertExists(String expertId) {
        return expertRepository.exists(expertId);
    }

    // Business logic for getting experts by analysis type (specialization mapping)
    public List<ForensicExpert> getExpertsByAnalysisType(String analysisType) {
        // Map analysis types to specializations (you can customize this mapping)
        String specialization = mapAnalysisTypeToSpecialization(analysisType);

        if (specialization != null) {
            return expertRepository.findBySpecialization(specialization);
        } else {
            // If no specific specialization, return all experts
            return getAllExperts();
        }
    }

    // Business logic for validating expert availability
    public boolean isExpertAvailable(String expertId) {
        // You can add business rules for expert availability here
        // For now, assume all experts are available
        return expertExists(expertId);
    }

    // Business logic for getting available experts count
    public int getAvailableExpertsCount() {
        return expertRepository.countAll();
    }

    // Helper method to map analysis types to specializations
    private String mapAnalysisTypeToSpecialization(String analysisType) {
        if (analysisType == null) return null;

        switch (analysisType.toLowerCase()) {
            case "dna analysis":
                return "DNA";
            case "fingerprint analysis":
                return "Fingerprints";
            case "digital forensics":
                return "Digital";
            case "ballistics analysis":
                return "Ballistics";
            case "toxicology screening":
                return "Toxicology";
            case "trace evidence analysis":
                return "Trace Evidence";
            case "document examination":
                return "Documents";
            default:
                return null;
        }
    }

    // Business validation for expert selection
    public void validateExpertSelection(String expertId) throws BusinessException {
        if (expertId == null || expertId.trim().isEmpty()) {
            throw new BusinessException("Please select a forensic expert");
        }

        if (!expertExists(expertId)) {
            throw new BusinessException("Selected forensic expert is not available");
        }

        if (!isExpertAvailable(expertId)) {
            throw new BusinessException("Selected forensic expert is currently unavailable");
        }
    }
}