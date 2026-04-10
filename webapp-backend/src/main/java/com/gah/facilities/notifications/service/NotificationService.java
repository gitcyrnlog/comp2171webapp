package com.gah.facilities.notifications.service;

import com.gah.facilities.facilities.domain.FacilityIssueReport;
import com.gah.facilities.laundry.domain.LaundryBooking;

public interface NotificationService {
    void sendLaundryBookingConfirmation(long residentId, LaundryBooking booking);

    void sendFacilityIssueConfirmation(long residentId, FacilityIssueReport report);

    void sendFacilityIssueAssignment(FacilityIssueReport report);
}
