package com.gah.facilities.laundry.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LaundryBooking {
    private final long id;
    private final long residentId;
    private final LocalDate bookingDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String machineNo;
    private final BookingStatus status;
    private final LocalDateTime createdAt;

    public LaundryBooking(long id, long residentId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime,
                          String machineNo, BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.residentId = residentId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.machineNo = machineNo;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getResidentId() {
        return residentId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getMachineNo() {
        return machineNo;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
