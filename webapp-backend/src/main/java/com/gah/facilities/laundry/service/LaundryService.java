package com.gah.facilities.laundry.service;

import com.gah.facilities.laundry.api.BookLaundryRequest;
import com.gah.facilities.laundry.domain.LaundryBooking;
import com.gah.facilities.laundry.repository.LaundryBookingRepository;
import com.gah.facilities.notifications.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class LaundryService {
    private final LaundryBookingRepository bookingRepository;
    private final NotificationService notificationService;

    public LaundryService(LaundryBookingRepository bookingRepository, NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
    }

    public boolean isAvailable(LocalDate bookingDate, LocalTime startTime, LocalTime endTime, String machineNo) {
        validateTimes(startTime, endTime);
        return !bookingRepository.existsOverlappingBooking(bookingDate, startTime, endTime, machineNo);
    }

    public LaundryBooking book(long residentId, BookLaundryRequest request) {
        validateTimes(request.startTime(), request.endTime());

        if (bookingRepository.existsResidentOverlappingBooking(
                residentId,
                request.bookingDate(),
                request.startTime(),
                request.endTime())) {
            throw new IllegalArgumentException("You already have an appointment during this time window");
        }

        if (!isAvailable(request.bookingDate(), request.startTime(), request.endTime(), request.machineNo())) {
            throw new IllegalArgumentException("Selected slot is unavailable for the machine");
        }

        LaundryBooking booking = bookingRepository.create(
                residentId,
                request.bookingDate(),
                request.startTime(),
                request.endTime(),
                request.machineNo()
        );
        notificationService.sendLaundryBookingConfirmation(residentId, booking);
        return booking;
    }

    public void cancel(long bookingId) {
        bookingRepository.cancel(bookingId);
    }

    private void validateTimes(LocalTime startTime, LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}
