package com.gah.facilities.facilities.repository.jdbc;

import com.gah.facilities.facilities.domain.FacilityIssueReport;
import com.gah.facilities.facilities.domain.FacilityIssueStatus;
import com.gah.facilities.facilities.repository.FacilityIssueRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JdbcFacilityIssueRepository implements FacilityIssueRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcFacilityIssueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<FacilityIssueReport> rowMapper = (rs, rowNum) -> new FacilityIssueReport(
            rs.getLong("id"),
            rs.getLong("resident_id"),
            rs.getString("location"),
            rs.getString("description"),
            FacilityIssueStatus.valueOf(rs.getString("status")),
            (Long) rs.getObject("assigned_worker_id"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );

    @Override
    public FacilityIssueReport create(long residentId, String location, String description) {
        String insert = """
                INSERT INTO facility_issue_reports(resident_id, location, description, status)
                VALUES (?, ?, ?, 'PENDING') RETURNING id
                """;
        Long id = jdbcTemplate.queryForObject(insert, Long.class, residentId, location, description);
        if (id == null) {
            throw new IllegalStateException("Failed to create facility issue report");
        }
        return jdbcTemplate.queryForObject("SELECT * FROM facility_issue_reports WHERE id = ?", rowMapper, id);
    }

    @Override
    public Optional<FacilityIssueReport> findById(long issueId) {
        return jdbcTemplate.query("SELECT * FROM facility_issue_reports WHERE id = ?", rowMapper, issueId)
                .stream()
                .findFirst();
    }

    @Override
    public FacilityIssueReport updateAssignmentAndStatus(long issueId, Long workerId, FacilityIssueStatus status) {
        jdbcTemplate.update(
                "UPDATE facility_issue_reports SET assigned_worker_id = ?, status = ?, updated_at = NOW() WHERE id = ?",
                workerId,
                status.name(),
                issueId
        );
        return findById(issueId).orElseThrow(() -> new IllegalStateException("Updated issue not found"));
    }
}
