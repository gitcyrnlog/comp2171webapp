package com.gah.facilities.auth.security;

import com.gah.facilities.common.domain.user.UserRole;

public record AuthenticatedUser(long userId, String email, UserRole role) {
}
