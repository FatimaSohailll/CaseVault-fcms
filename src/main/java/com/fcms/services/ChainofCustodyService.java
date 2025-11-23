package com.fcms.services;

import com.fcms.models.ChainOfCustody;
import com.fcms.repositories.ChainofCustodyRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ChainofCustodyService {
    private ChainofCustodyRepository custodyRepository;

    public ChainofCustodyService() {
        this.custodyRepository = new ChainofCustodyRepository();
    }

    // Business logic for adding a chain of custody record
    public void addCustodyRecord(ChainOfCustody record) throws BusinessException {
        // Business validation
        if (record.getEvidenceId() == null || record.getEvidenceId().trim().isEmpty()) {
            throw new BusinessException("Evidence ID is required for chain of custody");
        }
        if (record.getAction() == null || record.getAction().trim().isEmpty()) {
            throw new BusinessException("Action is required for chain of custody");
        }
        if (record.getDoneBy() == null || record.getDoneBy().trim().isEmpty()) {
            throw new BusinessException("Person responsible is required for chain of custody");
        }

        // Business rule: Generate record ID if not provided
        if (record.getRecordId() == null || record.getRecordId().trim().isEmpty()) {
            record.setRecordId(generateRecordId());
        }

        // Business rule: Set timestamp if not provided
        if (record.getTimestamp() == null) {
            record.setTimestamp(LocalDateTime.now());
        }

        // Delegate to repository
        custodyRepository.save(record);
    }

    // Business logic for recording evidence collection
    public void recordEvidenceCollection(String evidenceId, String collectedBy, String location) throws BusinessException {
        ChainOfCustody record = new ChainOfCustody();
        record.setRecordId(generateRecordId());
        record.setEvidenceId(evidenceId);
        record.setAction("Evidence Collected");
        record.setDoneBy(collectedBy);
        record.setTimestamp(LocalDateTime.now());

        addCustodyRecord(record);
    }

    // Business logic for recording evidence transfer
    public void recordEvidenceTransfer(String evidenceId, String fromPerson, String toPerson, String reason) throws BusinessException {
        ChainOfCustody record = new ChainOfCustody();
        record.setRecordId(generateRecordId());
        record.setEvidenceId(evidenceId);
        record.setAction("Evidence Transferred");
        record.setDoneBy(fromPerson);
        record.setTimestamp(LocalDateTime.now());

        addCustodyRecord(record);
    }

    // Business logic for recording forensic analysis
    public void recordForensicAnalysis(String evidenceId, String analyst, String analysisType, String findings) throws BusinessException {
        ChainOfCustody record = new ChainOfCustody();
        record.setRecordId(generateRecordId());
        record.setEvidenceId(evidenceId);
        record.setAction("Forensic Analysis - " + analysisType);
        record.setDoneBy(analyst);
        record.setTimestamp(LocalDateTime.now());

        addCustodyRecord(record);
    }

    // Business logic for recording evidence storage
    public void recordEvidenceStorage(String evidenceId, String storedBy, String storageLocation, String conditions) throws BusinessException {
        ChainOfCustody record = new ChainOfCustody();
        record.setRecordId(generateRecordId());
        record.setEvidenceId(evidenceId);
        record.setAction("Evidence Stored");
        record.setDoneBy(storedBy);
        record.setTimestamp(LocalDateTime.now());

        addCustodyRecord(record);
    }

    // Business logic for recording evidence disposal
    public void recordEvidenceDisposal(String evidenceId, String disposedBy, String disposalMethod, String authorization) throws BusinessException {
        ChainOfCustody record = new ChainOfCustody();
        record.setRecordId(generateRecordId());
        record.setEvidenceId(evidenceId);
        record.setAction("Evidence Disposed");
        record.setDoneBy(disposedBy);
        record.setTimestamp(LocalDateTime.now());

        addCustodyRecord(record);
    }

    // Business logic for updating a custody record
    public void updateCustodyRecord(ChainOfCustody record) throws BusinessException {
        // Business validation
        if (record.getRecordId() == null || record.getRecordId().trim().isEmpty()) {
            throw new BusinessException("Record ID is required for update");
        }
        if (record.getEvidenceId() == null || record.getEvidenceId().trim().isEmpty()) {
            throw new BusinessException("Evidence ID is required");
        }
        if (record.getAction() == null || record.getAction().trim().isEmpty()) {
            throw new BusinessException("Action is required");
        }
        if (record.getDoneBy() == null || record.getDoneBy().trim().isEmpty()) {
            throw new BusinessException("Person responsible is required");
        }

        // Check if record exists
        ChainOfCustody existingRecord = custodyRepository.findById(record.getRecordId());
        if (existingRecord == null) {
            throw new BusinessException("Chain of custody record not found with ID: " + record.getRecordId());
        }

        // Business rule: Cannot modify records older than 24 hours (for audit integrity)
        if (existingRecord.getTimestamp().isBefore(LocalDateTime.now().minusHours(24))) {
            throw new BusinessException("Cannot modify chain of custody records older than 24 hours for audit integrity");
        }

        custodyRepository.update(record);
    }

    // Business logic for deleting a custody record
    public void deleteCustodyRecord(String recordId) throws BusinessException {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new BusinessException("Record ID is required for deletion");
        }

        // Check if record exists
        ChainOfCustody existingRecord = custodyRepository.findById(recordId);
        if (existingRecord == null) {
            throw new BusinessException("Chain of custody record not found with ID: " + recordId);
        }

        // Business rule: Cannot delete records (maintain audit trail)
        throw new BusinessException("Chain of custody records cannot be deleted to maintain audit integrity. Use correction records instead.");
    }

    // Business logic for retrieving custody records by evidence
    public List<ChainOfCustody> getCustodyHistoryByEvidence(String evidenceId) throws BusinessException {
        if (evidenceId == null || evidenceId.trim().isEmpty()) {
            throw new BusinessException("Evidence ID is required");
        }

        return custodyRepository.findByEvidenceId(evidenceId);
    }

    // Business logic for retrieving custody record by ID
    public ChainOfCustody getCustodyRecordById(String recordId) throws BusinessException {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new BusinessException("Record ID is required");
        }

        ChainOfCustody record = custodyRepository.findById(recordId);
        if (record == null) {
            throw new BusinessException("Chain of custody record not found with ID: " + recordId);
        }

        return record;
    }

    // Business logic for retrieving all custody records
    public List<ChainOfCustody> getAllCustodyRecords() {
        return custodyRepository.findAll();
    }

    // Business logic for retrieving custody records by action
    public List<ChainOfCustody> getCustodyRecordsByAction(String action) throws BusinessException {
        if (action == null || action.trim().isEmpty()) {
            throw new BusinessException("Action is required");
        }

        return custodyRepository.findByAction(action);
    }

    // Business logic for retrieving custody records by personnel
    public List<ChainOfCustody> getCustodyRecordsByPersonnel(String personnel) throws BusinessException {
        if (personnel == null || personnel.trim().isEmpty()) {
            throw new BusinessException("Personnel name is required");
        }

        return custodyRepository.findByDoneBy(personnel);
    }

    // Business logic for retrieving custody records by date range
    public List<ChainOfCustody> getCustodyRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws BusinessException {
        if (startDate == null || endDate == null) {
            throw new BusinessException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }

        return custodyRepository.findByDateRange(startDate, endDate);
    }
    // Business logic for getting recent custody activities
    public List<ChainOfCustody> getRecentCustodyActivities(int days) throws BusinessException {
        if (days <= 0) {
            throw new BusinessException("Number of days must be positive");
        }

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime endDate = LocalDateTime.now();

        return custodyRepository.findByDateRange(startDate, endDate);
    }

    // Business logic for checking if evidence has custody records
    public boolean hasCustodyRecords(String evidenceId) throws BusinessException {
        if (evidenceId == null || evidenceId.trim().isEmpty()) {
            throw new BusinessException("Evidence ID is required");
        }

        return custodyRepository.getCountByEvidence(evidenceId) > 0;
    }

    // Business logic for getting custody record count
    public int getCustodyRecordCount(String evidenceId) throws BusinessException {
        if (evidenceId == null || evidenceId.trim().isEmpty()) {
            throw new BusinessException("Evidence ID is required");
        }

        return custodyRepository.getCountByEvidence(evidenceId);
    }

    // Business logic for getting all available actions
    public List<String> getAllAvailableActions() {
        return custodyRepository.getAllActions();
    }

    // Business logic for getting all personnel involved
    public List<String> getAllPersonnel() {
        return custodyRepository.getAllPersonnel();
    }

    // Business rule: Generate record ID
    private String generateRecordId() {
        return custodyRepository.getNextRecordId();
    }

    // Business logic for bulk operations
    public void addMultipleCustodyRecords(List<ChainOfCustody> records) throws BusinessException {
        for (ChainOfCustody record : records) {
            addCustodyRecord(record);
        }
    }
}