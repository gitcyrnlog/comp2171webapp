package com.gah.facilities.laundry.repository;

import com.gah.facilities.laundry.domain.LaundryBooking;

import java.time.LocalDate;
import java.time.LocalTime;

public interface LaundryBookingRepository {
    boolean existsOverlappingBooking(LocalDate date, LocalTime startTime, LocalTime endTime, String machineNo);

    LaundryBooking create(long residentId, LocalDate date, LocalTime startTime, LocalTime endTime, String machineNo);

    void cancel(long bookingId);
}
