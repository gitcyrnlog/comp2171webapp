package com.gah.facilities.residents.repository.jdbc;

import com.gah.facilities.common.domain.user.UserAccount;
import com.gah.facilities.common.domain.user.UserRole;
import com.gah.facilities.residents.repository.UserAccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcUserAccountRepository implements UserAccountRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<UserAccount> rowMapper = (rs, rowNum) -> new UserAccount(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("password_hash"),
            UserRole.valueOf(rs.getString("role")),
            rs.getString("block_code"),
            rs.getBoolean("active")
    );

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        String sql = "SELECT * FROM user_accounts WHERE username = ?";
        return jdbcTemplate.query(sql, rowMapper, username).stream().findFirst();
    }

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
    public UserAccount create(String username, String fullName, String email, String passwordHash, UserRole role, String blockCode) {
        String insert = "INSERT INTO user_accounts(username, full_name, email, password_hash, role, block_code, active) VALUES (?, ?, ?, ?, ?, ?, true)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, passwordHash);
            ps.setString(5, role.name());
            ps.setString(6, blockCode);
            return ps;
        }, keyHolder);

        Long id = null;
        try {
            Number singleKey = keyHolder.getKey();
            if (singleKey != null) {
                id = singleKey.longValue();
            }
        } catch (InvalidDataAccessApiUsageException ignored) {
            // Some drivers return multiple generated columns; resolve from key map below.
        }
        if (id == null) {
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.get("id") instanceof Number n) {
                id = n.longValue();
            }
        }
        if (id == null) {
            id = findByUsername(username).map(UserAccount::getId).orElse(null);
        }
        if (id == null) {
            throw new IllegalStateException("Failed to create user account");
        }
        return findById(id).orElseThrow(() -> new IllegalStateException("Created user not found"));
    }
}
