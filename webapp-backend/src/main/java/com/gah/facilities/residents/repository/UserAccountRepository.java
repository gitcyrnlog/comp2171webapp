package com.gah.facilities.residents.repository;

import com.gah.facilities.common.domain.user.UserAccount;
import com.gah.facilities.common.domain.user.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository {
    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findById(long id);

    List<UserAccount> findByRole(UserRole role);

    UserAccount create(String username, String fullName, String email, String passwordHash, UserRole role, String blockCode);
}
