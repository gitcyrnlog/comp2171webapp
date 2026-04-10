package com.gah.facilities.users.api;

import com.gah.facilities.common.domain.user.UserRole;

public record CurrentUserResponse(
        long userId,
        String username,
        String fullName,
        String email,
        UserRole role,
        String blockCode
) {
}
