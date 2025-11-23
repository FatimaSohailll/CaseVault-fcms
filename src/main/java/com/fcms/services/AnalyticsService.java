package com.fcms.services;

import com.fcms.models.Case;
import com.fcms.repositories.CaseRepository;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class AnalyticsService {

    private final CaseRepository repo = new CaseRepository();

    // MAIN DATA SOURCE
    public List<Case> getFilteredCases(LocalDate from, LocalDate to, String location, String type) {
        return repo.getFilteredCases(from, to, location, type);
    }

    public List<String> getAllLocations() {
        return repo.getAllLocations();
    }

    public List<String> getAllCrimeTypes() {
        return repo.getAllCaseTypes();
    }

    // ===================== STATISTICS =====================

    public double calculateMonthlyAverage(List<Case> cases) {
        if (cases.isEmpty()) return 0;

        Map<String, Integer> monthly = monthlyCounts(cases);
        return monthly.values().stream().mapToInt(i -> i).average().orElse(0);
    }

    public double closureRate(List<Case> cases) {
        if (cases.isEmpty()) return 0;

        long closed = cases.stream().filter(c -> c.getStatus().equalsIgnoreCase("closed")).count();
        return (closed * 100.0) / cases.size();
    }

    public int countCaseTypes(List<Case> cases) {
        return (int) cases.stream().map(Case::getType).distinct().count();
    }

    // ===================== CHART CALCULATIONS =====================

    public Map<String, Integer> monthlyCounts(List<Case> cases) {
        Map<String, Integer> map = new LinkedHashMap<>();

        for (int m = 1; m <= 12; m++) {
            String monthName = LocalDate.of(2024, m, 1).getMonth().getDisplayName(
                    TextStyle.SHORT, Locale.ENGLISH
            );
            map.put(monthName, 0);
        }

        for (Case c : cases) {
            String month = c.getDateRegistered().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            map.put(month, map.get(month) + 1);
        }

        return map;
    }

    public Map<String, Integer> typeDistribution(List<Case> cases) {
        Map<String, Integer> map = new HashMap<>();
        for (Case c : cases) {
            map.put(c.getType(), map.getOrDefault(c.getType(), 0) + 1);
        }
        return map;
    }

    public Map<String, Integer> locationDistribution(List<Case> cases) {
        Map<String, Integer> map = new HashMap<>();
        for (Case c : cases) {
            if (c.getLocation() != null)
                map.put(c.getLocation(), map.getOrDefault(c.getLocation(), 0) + 1);
        }
        return map;
    }

    // ===================== INSIGHTS =====================

    public String getMostCommonType(List<Case> cases) {
        return typeDistribution(cases).entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    public String getTopLocation(List<Case> cases) {
        return locationDistribution(cases).entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
    }

    public String getPeakMonth(List<Case> cases) {
        return monthlyCounts(cases).entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
    }
}
