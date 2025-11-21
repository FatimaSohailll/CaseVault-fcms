package com.fcms.repositories;

import com.fcms.models.ForensicReport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportRepository {
    private List<ForensicReport> reports;

    public ReportRepository() {
        this.reports = new ArrayList<>();
    }

    public void save(ForensicReport report) {
        reports.add(report);
        System.out.println("Saved forensic report: " + report.getReportId());
    }

    public void update(ForensicReport report) {
        for (int i = 0; i < reports.size(); i++) {
            if (reports.get(i).getReportId().equals(report.getReportId())) {
                reports.set(i, report);
                System.out.println("Updated forensic report: " + report.getReportId());
                return;
            }
        }
        throw new RuntimeException("Forensic report not found: " + report.getReportId());
    }

    public ForensicReport findById(String reportId) {
        return reports.stream()
                .filter(r -> r.getReportId().equals(reportId))
                .findFirst()
                .orElse(null);
    }

    public List<ForensicReport> findByRequestId(String requestId) {
        return reports.stream()
                .filter(r -> requestId.equals(r.getRequestId()))
                .collect(Collectors.toList());
    }
}