package Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private final String username;
    private final LocalDate date;
    private final String timeSlot;
    private final String location;
    private final String machineNo;
    private final LocalDateTime createdAt;

    public Appointment(int id, String username, LocalDate date, String timeSlot, String location,
            String machineNo, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.date = date;
        this.timeSlot = timeSlot;
        this.location = location;
        this.machineNo = machineNo;
        this.createdAt = createdAt;
    }

    public Appointment(String username, LocalDate date, String timeSlot, String location, String machineNo) {
        this(-1, username, date, timeSlot, location, machineNo, LocalDateTime.now());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public LocalDate getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public String getLocation() { return location; }
    public String getMachineNo() { return machineNo; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return date + "  |  " + location + "  |  " + timeSlot + "  |  Machine " + machineNo;
    }
}
