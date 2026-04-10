package com.gah.facilities.laundry.repository.jdbc;

import com.gah.facilities.laundry.domain.BookingStatus;
import com.gah.facilities.laundry.domain.LaundryBooking;
import com.gah.facilities.laundry.repository.LaundryBookingRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public class JdbcLaundryBookingRepository implements LaundryBookingRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcLaundryBookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<LaundryBooking> rowMapper = (rs, rowNum) -> new LaundryBooking(
            rs.getLong("id"),
            rs.getLong("resident_id"),
            rs.getDate("booking_date").toLocalDate(),
            rs.getTime("start_time").toLocalTime(),
            rs.getTime("end_time").toLocalTime(),
            rs.getString("machine_no"),
            BookingStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    @Override
    public boolean existsOverlappingBooking(LocalDate date, LocalTime startTime, LocalTime endTime, String machineNo) {
        String sql = """
                SELECT COUNT(*)
                FROM laundry_bookings
                WHERE booking_date = ?
                  AND machine_no = ?
                  AND status = 'BOOKED'
                  AND (? < end_time AND ? > start_time)
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, date, machineNo, startTime, endTime);
        return count != null && count > 0;
    }

    @Override
    public boolean existsResidentOverlappingBooking(long residentId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        String sql = """
                SELECT COUNT(*)
                FROM laundry_bookings
                WHERE resident_id = ?
                  AND booking_date = ?
                  AND status = 'BOOKED'
                  AND (? < end_time AND ? > start_time)
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, residentId, date, startTime, endTime);
        return count != null && count > 0;
    }

    @Override
    public LaundryBooking create(long residentId, LocalDate date, LocalTime startTime, LocalTime endTime, String machineNo) {
        String insert = """
                INSERT INTO laundry_bookings(resident_id, booking_date, start_time, end_time, machine_no, status)
                VALUES (?, ?, ?, ?, ?, 'BOOKED') RETURNING id
                """;
        Long id = jdbcTemplate.queryForObject(insert, Long.class, residentId, date, startTime, endTime, machineNo);
        if (id == null) {
            throw new IllegalStateException("Failed to create laundry booking");
        }
        return jdbcTemplate.queryForObject("SELECT * FROM laundry_bookings WHERE id = ?", rowMapper, id);
    }

    @Override
    public void cancel(long bookingId) {
        jdbcTemplate.update("UPDATE laundry_bookings SET status = 'CANCELLED' WHERE id = ?", bookingId);
    }
}
