package com.gah.facilities.laundry.api;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookLaundryRequest(
        @NotNull @FutureOrPresent LocalDate bookingDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotBlank String machineNo
) {
}
