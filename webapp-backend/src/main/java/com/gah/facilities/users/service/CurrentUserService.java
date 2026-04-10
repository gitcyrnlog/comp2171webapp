package com.gah.facilities.users.service;

import com.gah.facilities.auth.security.AuthenticatedUser;
import com.gah.facilities.common.domain.user.UserAccount;
import com.gah.facilities.residents.repository.UserAccountRepository;
import com.gah.facilities.users.api.CurrentUserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserAccountRepository userAccountRepository;

    public CurrentUserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public CurrentUserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {
            throw new IllegalArgumentException("Unauthenticated request");
        }

        UserAccount account = userAccountRepository.findById(principal.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new CurrentUserResponse(
                account.getId(),
                account.getFullName(),
                account.getEmail(),
                account.getRole(),
                account.getBlockCode()
        );
    }
}
