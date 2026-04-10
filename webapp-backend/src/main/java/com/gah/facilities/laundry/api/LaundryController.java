package com.gah.facilities.laundry.api;

import com.gah.facilities.auth.security.AuthenticatedUser;
import com.gah.facilities.laundry.domain.LaundryBooking;
import com.gah.facilities.laundry.service.LaundryService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/laundry")
public class LaundryController {
    private final LaundryService laundryService;

    public LaundryController(LaundryService laundryService) {
        this.laundryService = laundryService;
    }

    @GetMapping("/availability")
    @PreAuthorize("hasAnyRole('RESIDENT','BLOCK_REPRESENTATIVE','ADMIN')")
    public ResponseEntity<LaundryAvailabilityResponse> getAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam String machineNo
    ) {
        boolean available = laundryService.isAvailable(bookingDate, startTime, endTime, machineNo);
        return ResponseEntity.ok(new LaundryAvailabilityResponse(available));
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasAnyRole('RESIDENT','BLOCK_REPRESENTATIVE','ADMIN')")
    public ResponseEntity<LaundryBooking> createBooking(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody BookLaundryRequest request) {
        return ResponseEntity.ok(laundryService.book(authenticatedUser.userId(), request));
    }

    @DeleteMapping("/bookings/{bookingId}")
    @PreAuthorize("hasAnyRole('RESIDENT','BLOCK_REPRESENTATIVE','ADMIN')")
    public ResponseEntity<Void> cancelBooking(@PathVariable long bookingId) {
        laundryService.cancel(bookingId);
        return ResponseEntity.noContent().build();
    }
}
