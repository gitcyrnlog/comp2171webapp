package com.gah.facilities.securityops.domain;

import java.time.LocalDateTime;

public class SecurityIssueReport {
    private final long id;
    private final long reporterId;
    private final String title;
    private final String description;
    private final String status;
    private final LocalDateTime createdAt;

    public SecurityIssueReport(long id, long reporterId, String title, String description, String status, LocalDateTime createdAt) {
        this.id = id;
        this.reporterId = reporterId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getReporterId() {
        return reporterId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
