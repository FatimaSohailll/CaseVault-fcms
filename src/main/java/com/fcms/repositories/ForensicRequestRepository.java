package com.fcms.repositories;

import com.fcms.models.ForensicRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ForensicRequestRepository {
    private List<ForensicRequest> requests;

    public ForensicRequestRepository() {
        this.requests = new ArrayList<>();
    }

    public void save(ForensicRequest request) {
        requests.add(request);
        System.out.println("Saved forensic request: " + request.getRequestId());
    }

    public void update(ForensicRequest request) {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getRequestId().equals(request.getRequestId())) {
                requests.set(i, request);
                System.out.println("Updated forensic request: " + request.getRequestId());
                return;
            }
        }
        throw new RuntimeException("Forensic request not found: " + request.getRequestId());
    }

    public List<ForensicRequest> findAll() {
        return new ArrayList<>(requests);
    }

    public ForensicRequest findById(String requestId) {
        return requests.stream()
                .filter(r -> r.getRequestId().equals(requestId))
                .findFirst()
                .orElse(null);
    }

    public List<ForensicRequest> findByStatus(String status) {
        return requests.stream()
                .filter(r -> status.equals(r.getStatus()))
                .collect(Collectors.toList());
    }
}