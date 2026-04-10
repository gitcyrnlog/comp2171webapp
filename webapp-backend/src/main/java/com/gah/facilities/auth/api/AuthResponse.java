package com.gah.facilities.auth.api;

import com.gah.facilities.common.domain.user.UserRole;

public record AuthResponse(
        long userId,
        String username,
        String fullName,
        String email,
        UserRole role,
        String accessToken
) {
}
