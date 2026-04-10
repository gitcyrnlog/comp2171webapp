package com.gah.facilities.notifications.service;

import com.gah.facilities.facilities.domain.FacilityIssueReport;
import com.gah.facilities.laundry.domain.LaundryBooking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingNotificationService implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationService.class);

    @Override
    public void sendLaundryBookingConfirmation(long residentId, LaundryBooking booking) {
        log.info("Laundry booking confirmed: residentId={}, bookingId={}, date={}", residentId, booking.getId(), booking.getBookingDate());
    }

    @Override
    public void sendFacilityIssueConfirmation(long residentId, FacilityIssueReport report) {
        log.info("Facility issue submitted: residentId={}, issueId={}, status={}", residentId, report.getId(), report.getStatus());
    }

    @Override
    public void sendFacilityIssueAssignment(FacilityIssueReport report) {
        log.info("Facility issue assignment updated: issueId={}, workerId={}, status={}", report.getId(), report.getAssignedWorkerId(), report.getStatus());
    }
}
