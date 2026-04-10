package com.gah.facilities.facilities.repository;

import com.gah.facilities.facilities.domain.FacilityIssueReport;
import com.gah.facilities.facilities.domain.FacilityIssueStatus;

import java.util.Optional;

public interface FacilityIssueRepository {
    FacilityIssueReport create(long residentId, String location, String description);

    Optional<FacilityIssueReport> findById(long issueId);

    FacilityIssueReport updateAssignmentAndStatus(long issueId, Long workerId, FacilityIssueStatus status);
}
