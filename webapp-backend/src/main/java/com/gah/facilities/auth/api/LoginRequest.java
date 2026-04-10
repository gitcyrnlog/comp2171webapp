package com.gah.facilities.auth.api;

import jakarta.validation.constraints.Email;

public record LoginRequest(
        String username,
        @Email String email,
        String password
) {
}
