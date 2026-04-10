package com.gah.facilities.facilities.domain;

import java.time.LocalDateTime;

public class FacilityIssueReport {
    private final long id;
    private final long residentId;
    private final String location;
    private final String description;
    private final FacilityIssueStatus status;
    private final Long assignedWorkerId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public FacilityIssueReport(long id, long residentId, String location, String description, FacilityIssueStatus status,
                               Long assignedWorkerId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.residentId = residentId;
        this.location = location;
        this.description = description;
        this.status = status;
        this.assignedWorkerId = assignedWorkerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public long getResidentId() {
        return residentId;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public FacilityIssueStatus getStatus() {
        return status;
    }

    public Long getAssignedWorkerId() {
        return assignedWorkerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
