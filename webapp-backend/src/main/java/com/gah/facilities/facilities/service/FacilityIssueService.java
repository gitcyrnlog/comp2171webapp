package com.gah.facilities.facilities.service;

import com.gah.facilities.common.domain.user.UserAccount;
import com.gah.facilities.common.domain.user.UserRole;
import com.gah.facilities.facilities.api.CreateFacilityIssueRequest;
import com.gah.facilities.facilities.domain.FacilityIssueReport;
import com.gah.facilities.facilities.domain.FacilityIssueStatus;
import com.gah.facilities.facilities.repository.FacilityIssueRepository;
import com.gah.facilities.notifications.service.NotificationService;
import com.gah.facilities.residents.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityIssueService {
    private final FacilityIssueRepository facilityIssueRepository;
    private final UserAccountRepository userAccountRepository;
    private final NotificationService notificationService;

    public FacilityIssueService(FacilityIssueRepository facilityIssueRepository,
                                UserAccountRepository userAccountRepository,
                                NotificationService notificationService) {
        this.facilityIssueRepository = facilityIssueRepository;
        this.userAccountRepository = userAccountRepository;
        this.notificationService = notificationService;
    }

    public FacilityIssueReport createIssue(long residentId, CreateFacilityIssueRequest request) {
        FacilityIssueReport created = facilityIssueRepository.create(
                residentId,
                request.location(),
                request.description()
        );

        List<UserAccount> workers = userAccountRepository.findByRole(UserRole.MAINTENANCE_WORKER);
        FacilityIssueReport updated;
        if (workers.isEmpty()) {
            updated = created;
        } else {
            long assignedWorkerId = workers.get(0).getId();
            updated = facilityIssueRepository.updateAssignmentAndStatus(
                    created.getId(),
                    assignedWorkerId,
                    FacilityIssueStatus.IN_PROGRESS
            );
            notificationService.sendFacilityIssueAssignment(updated);
        }

        notificationService.sendFacilityIssueConfirmation(residentId, updated);
        return updated;
    }

    public FacilityIssueReport getIssue(long issueId) {
        return facilityIssueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found"));
    }
}
