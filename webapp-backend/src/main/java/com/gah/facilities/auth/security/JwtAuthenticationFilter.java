package com.gah.facilities.auth.security;

import com.gah.facilities.auth.service.TokenService;
import com.gah.facilities.common.domain.user.UserAccount;
import com.gah.facilities.residents.repository.UserAccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UserAccountRepository userAccountRepository;

    public JwtAuthenticationFilter(TokenService tokenService, UserAccountRepository userAccountRepository) {
        this.tokenService = tokenService;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenService.parseAndValidate(token).ifPresent(claims -> {
                UserAccount account = userAccountRepository.findById(claims.userId()).orElse(null);
                if (account != null && account.isActive()) {
                    AuthenticatedUser principal = new AuthenticatedUser(account.getId(), account.getEmail(), account.getRole());
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            });
        }

        filterChain.doFilter(request, response);
    }
}
