package com.gah.facilities.facilities.api;

import com.gah.facilities.auth.security.AuthenticatedUser;
import com.gah.facilities.facilities.domain.FacilityIssueReport;
import com.gah.facilities.facilities.service.FacilityIssueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/facilities/issues")
public class FacilityIssueController {
    private final FacilityIssueService issueService;

    public FacilityIssueController(FacilityIssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RESIDENT','BLOCK_REPRESENTATIVE','ADMIN')")
    public ResponseEntity<FacilityIssueReport> createIssue(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody CreateFacilityIssueRequest request) {
        return ResponseEntity.ok(issueService.createIssue(authenticatedUser.userId(), request));
    }

    @GetMapping("/{issueId}")
    @PreAuthorize("hasAnyRole('RESIDENT','BLOCK_REPRESENTATIVE','ADMIN','MAINTENANCE_WORKER')")
    public ResponseEntity<FacilityIssueReport> getIssue(@PathVariable long issueId) {
        return ResponseEntity.ok(issueService.getIssue(issueId));
    }
}
