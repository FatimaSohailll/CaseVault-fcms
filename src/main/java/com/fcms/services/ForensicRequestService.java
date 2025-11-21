package com.fcms.services;

import com.fcms.models.ForensicRequest;
import com.fcms.repositories.ForensicRequestRepository;
import java.util.List;

public class ForensicRequestService {
    private ForensicRequestRepository requestRepository;

    public ForensicRequestService() {
        this.requestRepository = new ForensicRequestRepository();
    }

    // Business logic for creating forensic request
    public void createRequest(ForensicRequest request) throws BusinessException {
        // Business validation
        if (request.getCaseId() == null || request.getCaseId().trim().isEmpty()) {
            throw new BusinessException("Case ID is required");
        }
        if (request.getAnalysisType() == null || request.getAnalysisType().trim().isEmpty()) {
            throw new BusinessException("Analysis type is required");
        }
        if (request.getEvidenceIds() == null || request.getEvidenceIds().isEmpty()) {
            throw new BusinessException("At least one evidence item must be selected");
        }

        // Business rule: Set default priority if not provided
        if (request.getPriority() == null) {
            request.setPriority("Normal");
        }

        // Business rule: Generate request ID
        request.setRequestId(generateRequestId());

        // Business rule: Set initial status
        request.setStatus("Pending");

        // Delegate to repository
        requestRepository.save(request);
    }

    // Business logic for updating request status
    public void updateRequestStatus(String requestId, String status) throws BusinessException {
        ForensicRequest request = requestRepository.findById(requestId);
        if (request == null) {
            throw new BusinessException("Forensic request not found");
        }

        // Business rule: Validate status transition
        if (!isValidStatusTransition(request.getStatus(), status)) {
            throw new BusinessException("Invalid status transition from " + request.getStatus() + " to " + status);
        }

        request.setStatus(status);
        requestRepository.update(request);
    }

    // Business logic for retrieving all requests
    public List<ForensicRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    // Business logic for getting requests by status
    public List<ForensicRequest> getRequestsByStatus(String status) {
        return requestRepository.findByStatus(status);
    }

    // Business rule: Validate status transitions
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case "Pending":
                return "In Progress".equals(newStatus) || "Cancelled".equals(newStatus);
            case "In Progress":
                return "Completed".equals(newStatus) || "Cancelled".equals(newStatus);
            case "Completed":
            case "Cancelled":
                return false; // Cannot change from final states
            default:
                return true;
        }
    }

    // Business rule: Generate request ID
    private String generateRequestId() {
        List<ForensicRequest> requests = requestRepository.findAll();
        int nextId = requests.size() + 1;
        return "FR-" + String.format("%03d", nextId);
    }
}