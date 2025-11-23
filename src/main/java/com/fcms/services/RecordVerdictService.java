package com.fcms.services;

import com.fcms.models.CourtVerdict;
import com.fcms.repositories.CourtVerdictRepository;

import java.time.LocalDate;
import java.util.UUID;

public class RecordVerdictService {

    public String[] getVerdictOptions() {
        return new String[]{"guilty", "not guilty"};
    }

    public CourtVerdict getVerdictForCase(String caseId) {
        return CourtVerdictRepository.getVerdict(caseId);
    }
    public boolean hasVerdict(String caseId) {
        return CourtVerdictRepository.hasVerdict(caseId);
    }

    public boolean saveVerdict(String outcome,
                               String sentence,
                               String notes,
                               LocalDate date,
                               String caseId,
                               String issuedBy) {

        // Create verdict object
        CourtVerdict v = new CourtVerdict(
                UUID.randomUUID().toString(),
                outcome,
                sentence,
                date,
                notes,
                caseId,
                issuedBy
        );

        // Save using repository
        return CourtVerdictRepository.saveVerdict(v);
    }
}
