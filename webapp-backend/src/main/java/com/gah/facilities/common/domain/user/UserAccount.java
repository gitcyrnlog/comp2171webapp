package com.gah.facilities.common.domain.user;

public class UserAccount {
    private final long id;
    private final String username;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final String blockCode;
    private final boolean active;

    public UserAccount(long id, String username, String fullName, String email, String passwordHash, UserRole role, String blockCode, boolean active) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.blockCode = blockCode;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public boolean isActive() {
        return active;
    }
}
