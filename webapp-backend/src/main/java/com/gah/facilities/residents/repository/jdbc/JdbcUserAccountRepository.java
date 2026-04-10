package com.gah.facilities.residents.repository.jdbc;

import com.gah.facilities.common.domain.user.UserAccount;
import com.gah.facilities.common.domain.user.UserRole;
import com.gah.facilities.residents.repository.UserAccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserAccountRepository implements UserAccountRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<UserAccount> rowMapper = (rs, rowNum) -> new UserAccount(
            rs.getLong("id"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("password_hash"),
            UserRole.valueOf(rs.getString("role")),
            rs.getString("block_code"),
            rs.getBoolean("active")
    );

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        String sql = "SELECT * FROM user_accounts WHERE email = ?";
        return jdbcTemplate.query(sql, rowMapper, email).stream().findFirst();
    }

    @Override
    public Optional<UserAccount> findById(long id) {
        String sql = "SELECT * FROM user_accounts WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    @Override
    public List<UserAccount> findByRole(UserRole role) {
        String sql = "SELECT * FROM user_accounts WHERE role = ? AND active = true";
        return jdbcTemplate.query(sql, rowMapper, role.name());
    }

    @Override
    public UserAccount create(String fullName, String email, String passwordHash, UserRole role, String blockCode) {
        String insert = "INSERT INTO user_accounts(full_name, email, password_hash, role, block_code, active) VALUES (?, ?, ?, ?, ?, true) RETURNING id";
        Long id = jdbcTemplate.queryForObject(insert, Long.class, fullName, email, passwordHash, role.name(), blockCode);
        if (id == null) {
            throw new IllegalStateException("Failed to create user account");
        }
        return findById(id).orElseThrow(() -> new IllegalStateException("Created user not found"));
    }
}
