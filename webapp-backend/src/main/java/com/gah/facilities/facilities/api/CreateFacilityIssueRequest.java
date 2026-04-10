package com.gah.facilities.facilities.api;

import jakarta.validation.constraints.NotBlank;

public record CreateFacilityIssueRequest(
        @NotBlank String location,
        @NotBlank String description
) {
}
