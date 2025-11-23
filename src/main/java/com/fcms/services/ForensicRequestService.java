package com.fcms.services;

import com.fcms.models.ForensicRequest;
import com.fcms.repositories.ForensicRequestRepository;
import java.sql.SQLException;
import java.util.List;

public class ForensicRequestService {
    private ForensicRequestRepository requestRepository;
    private String expertID;

    public ForensicRequestService() {
        // Pass the expert ID when creating the repository
        this.requestRepository = new ForensicRequestRepository(expertID);
    }

    public ForensicRequestService(String expertId) {
        this.requestRepository = new ForensicRequestRepository(expertId);
    }

    // Business logic for creating forensic request
    public void createRequest(ForensicRequest request) throws BusinessException {
        try {
            // Business validation
            if (request.getAnalysisType() == null || request.getAnalysisType().trim().isEmpty()) {
                throw new BusinessException("Analysis type is required");
            }
            if (request.getEvidenceId() == null || request.getEvidenceId().trim().isEmpty()) {
                throw new BusinessException("Evidence ID is required");
            }
            if (request.getExpertId() == null || request.getExpertId().trim().isEmpty()) {
                throw new BusinessException("Expert ID is required");
            }

            // Business rule: Set default priority if not provided
            if (request.getPriority() == null) {
                request.setPriority("Medium");
            }

            // Business rule: Generate request ID
            request.setRequestId(requestRepository.generateRequestId());

            // Business rule: Set initial status
            request.setStatus("pending");

            // Delegate to repository
            requestRepository.save(request);
        } catch (SQLException e) {
            throw new BusinessException("Database error while creating request: " + e.getMessage());
        }
    }

    // Business logic for updating request status
    public void updateRequestStatus(String requestId, String status) throws BusinessException {
        try {
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
        } catch (SQLException e) {
            throw new BusinessException("Database error while updating request status: " + e.getMessage());
        }
    }

    // Business logic for retrieving all requests for current expert
    public List<ForensicRequest> getAllRequests() throws BusinessException {
        try {
            return requestRepository.findAll();
        } catch (SQLException e) {
            throw new BusinessException("Database error while retrieving requests: " + e.getMessage());
        }
    }

    // Business logic for getting requests by status for current expert
    public List<ForensicRequest> getRequestsByStatus(String status) throws BusinessException {
        try {
            return requestRepository.findByStatus(status);
        } catch (SQLException e) {
            throw new BusinessException("Database error while retrieving requests by status: " + e.getMessage());
        }
    }

    // Business logic for getting request by ID for current expert
    public ForensicRequest getRequestById(String requestId) throws BusinessException {
        try {
            ForensicRequest request = requestRepository.findById(requestId);
            if (request == null) {
                throw new BusinessException("Forensic request not found: " + requestId);
            }
            return request;
        } catch (SQLException e) {
            throw new BusinessException("Database error while retrieving request: " + e.getMessage());
        }
    }

    // Analytics methods for dashboard - for current expert
    public int getPendingCount() throws BusinessException {
        try {
            return requestRepository.getPendingCount();
        } catch (SQLException e) {
            throw new BusinessException("Database error while getting pending count: " + e.getMessage());
        }
    }

    public int getUrgentCount() throws BusinessException {
        try {
            return requestRepository.getUrgentCount();
        } catch (SQLException e) {
            throw new BusinessException("Database error while getting urgent count: " + e.getMessage());
        }
    }

    public int getCompletedCount() throws BusinessException {
        try {
            return requestRepository.getCompletedCount();
        } catch (SQLException e) {
            throw new BusinessException("Database error while getting completed count: " + e.getMessage());
        }
    }

    public int getTotalCount() throws BusinessException {
        try {
            return requestRepository.getTotalCount();
        } catch (SQLException e) {
            throw new BusinessException("Database error while getting total count: " + e.getMessage());
        }
    }

    // Business rule: Validate status transitions
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Define valid status transitions based on your database constraints
        // Only 'pending' and 'completed' are allowed in database
        switch (currentStatus.toLowerCase()) {
            case "pending":
                return "completed".equalsIgnoreCase(newStatus);
            case "completed":
                return false; // Cannot change from completed state
            default:
                return true; // Allow any transition for unknown states
        }
    }

    // Business logic to check if request can be updated
    public boolean canUpdateRequest(String requestId) throws BusinessException {
        try {
            ForensicRequest request = requestRepository.findById(requestId);
            if (request == null) {
                return false;
            }
            return !request.isCompleted();
        } catch (SQLException e) {
            throw new BusinessException("Database error while checking request update status: " + e.getMessage());
        }
    }

    // Method to get current expert ID
    public void setExpertID(String expertID) {this.expertID = expertID;}

    public String getCurrentExpertId() {
        return expertID;
    }
}