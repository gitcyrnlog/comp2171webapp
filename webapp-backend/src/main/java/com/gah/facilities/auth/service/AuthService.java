package com.gah.facilities.auth.service;

import com.gah.facilities.auth.api.AuthResponse;
import com.gah.facilities.auth.api.LoginRequest;
import com.gah.facilities.auth.api.RegisterRequest;
import com.gah.facilities.common.domain.user.UserAccount;
import com.gah.facilities.common.domain.user.UserRole;
import com.gah.facilities.residents.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AuthResponse login(LoginRequest request) {
        if ((request.username() == null || request.username().isBlank())
                && (request.email() == null || request.email().isBlank())) {
            throw new IllegalArgumentException("Provide a username or email");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        Optional<UserAccount> maybeUser = (request.username() != null && !request.username().isBlank())
                ? userAccountRepository.findByUsername(request.username())
                : userAccountRepository.findByEmail(request.email());

        UserAccount user = maybeUser.orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return new AuthResponse(
                user.getId(),
            user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                tokenService.issueToken(user)
        );
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.role() != UserRole.RESIDENT && request.role() != UserRole.BLOCK_REPRESENTATIVE) {
            throw new IllegalArgumentException("Only resident and block representative self-registration is allowed");
        }

        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        userAccountRepository.findByUsername(request.username()).ifPresent(existing -> {
            throw new IllegalArgumentException("Username already registered");
        });

        userAccountRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new IllegalArgumentException("Email already registered");
        });

        UserAccount created = userAccountRepository.create(
                request.username(),
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.role(),
                request.blockCode()
        );

        return new AuthResponse(
                created.getId(),
            created.getUsername(),
                created.getFullName(),
                created.getEmail(),
                created.getRole(),
                tokenService.issueToken(created)
        );
    }
}
