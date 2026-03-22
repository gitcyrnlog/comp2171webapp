package Data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import Application.Notification.EmailData;
import Application.Notification.Notification;
import Base.Person;
import Base.Resident;
import Base.Staff;
import Business.Task;
import Business.TimeTrack;

public class DatabaseManager {

    private Connection connection;

    public DatabaseManager() {
        connect();
        initSchema();
        seedDefaultUsers();
    }

    private void initSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS appointments (" +
                "id SERIAL PRIMARY KEY," +
                "username VARCHAR(100) REFERENCES users(username)," +
                "appt_date DATE NOT NULL," +
                "time_slot VARCHAR(50) NOT NULL," +
                "location VARCHAR(50) NOT NULL DEFAULT ''," +
                "machine_no VARCHAR(20) NOT NULL," +
                "created_at TIMESTAMP NOT NULL DEFAULT NOW()" +
                ")";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("db.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to PostgreSQL.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    private void seedDefaultUsers() {
        if (getUsers().isEmpty()) {
            System.out.println("No users found. Creating default users...");
            addItem(new Resident("Orville Daley", "odspam23@gmail.com", "620164974", "password123", "I1234"));
            addItem(new Staff("Admin", "gahfacilities@gmail.com", "admin", "admin"));
        }
    }

    // ── Users ────────────────────────────────────────────────────────────────

    public void addItem(Person person) {
        String sql = "INSERT INTO users (username, name, email, password, role, room_number) " +
                "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (username) DO NOTHING";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, person.getUsername());
            ps.setString(2, person.getName());
            ps.setString(3, person.getEmail());
            ps.setString(4, person.getPassword());
            ps.setString(5, person.getRole());
            ps.setString(6, person instanceof Resident ? ((Resident) person).getRoomNumber() : null);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateUser(Person person) {
        String sql = "UPDATE users SET password = ?, email = ? WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, person.getPassword());
            ps.setString(2, person.getEmail());
            ps.setString(3, person.getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Person> getUsers() {
        ArrayList<Person> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String role = rs.getString("role");
                Person p;
                if ("resident".equals(role)) {
                    p = new Resident(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("room_number"));
                } else {
                    p = new Staff(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("username"),
                            rs.getString("password"));
                }
                users.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // ── Tasks ────────────────────────────────────────────────────────────────

    public void addItem(Task task) {
        String sql = "INSERT INTO tasks (task_name, description, category, room_num, priority, status, " +
                "complete, reporter, assignee, created_at, completed_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING task_id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, task.getTask_name());
            ps.setString(2, task.getTask_Description());
            ps.setString(3, task.getTask_Category());
            ps.setString(4, task.getRoomNum());
            ps.setInt(5, task.getTask_Priority());
            ps.setString(6, task.getStatus());
            ps.setBoolean(7, task.getComplete());
            ps.setString(8, task.reporter != null ? task.reporter.getUsername() : null);
            ps.setNull(9, java.sql.Types.VARCHAR);
            ps.setTimestamp(10, Timestamp.valueOf(task.getTimeTrack().getCreateTime()));
            LocalDateTime completedAt = task.getTimeTrack().getCompleteTime();
            ps.setTimestamp(11, completedAt != null ? Timestamp.valueOf(completedAt) : null);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                task.setTASKID(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reloadTasks(ArrayList<Task> tasks) {
        String sql = "UPDATE tasks SET complete = ?, completed_at = ?, assignee = ?, status = ?, priority = ? " +
                "WHERE task_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Task task : tasks) {
                ps.setBoolean(1, task.getComplete());
                LocalDateTime completedAt = task.getTimeTrack().getCompleteTime();
                ps.setTimestamp(2, completedAt != null ? Timestamp.valueOf(completedAt) : null);
                ps.setNull(3, java.sql.Types.VARCHAR);
                ps.setString(4, task.getStatus());
                ps.setInt(5, task.getTask_Priority());
                ps.setInt(6, task.getTASKID());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Map<String, Person> userMap = new HashMap<>();
        for (Person p : getUsers()) {
            userMap.put(p.getUsername(), p);
        }

        String sql = "SELECT * FROM tasks";
        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp createdAt = rs.getTimestamp("created_at");
                Timestamp completedAt = rs.getTimestamp("completed_at");
                TimeTrack time = new TimeTrack(
                        createdAt.toLocalDateTime(),
                        completedAt != null ? completedAt.toLocalDateTime() : null);

                Task task = new Task(
                        rs.getInt("task_id"),
                        rs.getString("task_name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("room_num"),
                        rs.getInt("priority"),
                        rs.getString("status"),
                        rs.getBoolean("complete"),
                        time);

                String reporterUsername = rs.getString("reporter");
                if (reporterUsername != null && userMap.get(reporterUsername) instanceof Resident) {
                    task.reporter = (Resident) userMap.get(reporterUsername);
                }

                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // ── Notifications ─────────────────────────────────────────────────────────

    public void addItem(Notification notification) {
        String sql = "INSERT INTO notifications (recipient, subject, body) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            Person recipient = notification.getRecipient();
            ps.setString(1, recipient != null ? recipient.getUsername() : null);
            ps.setString(2, notification.getSubjectLine());
            ps.setString(3, "");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Notification> getNotifications() {
        ArrayList<Notification> notifications = new ArrayList<>();
        Map<String, Person> userMap = new HashMap<>();
        for (Person p : getUsers()) {
            userMap.put(p.getUsername(), p);
        }

        String sql = "SELECT * FROM notifications";
        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String recipientUsername = rs.getString("recipient");
                Person recipient = userMap.get(recipientUsername);

                EmailData emailData = new EmailData();
                emailData.setRecipient(recipient);
                emailData.setSubject(rs.getString("subject"));
                emailData.setText(rs.getString("body"));

                notifications.add(new Notification(emailData));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    // ── Appointments ──────────────────────────────────────────────────────────

    public void addAppointment(Appointment appt) {
        String sql = "INSERT INTO appointments (username, appt_date, time_slot, location, machine_no, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, appt.getUsername());
            ps.setDate(2, Date.valueOf(appt.getDate()));
            ps.setString(3, appt.getTimeSlot());
            ps.setString(4, appt.getLocation());
            ps.setString(5, appt.getMachineNo());
            ps.setTimestamp(6, Timestamp.valueOf(appt.getCreatedAt()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                appt.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isSlotTaken(java.time.LocalDate date, String timeSlot, String location, String machineNo) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appt_date = ? AND time_slot = ? AND location = ? AND machine_no = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, timeSlot);
            ps.setString(3, location);
            ps.setString(4, machineNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appt_date, time_slot";
        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Appointment(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getDate("appt_date").toLocalDate(),
                        rs.getString("time_slot"),
                        rs.getString("location"),
                        rs.getString("machine_no"),
                        rs.getTimestamp("created_at").toLocalDateTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteAppointment(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task task) {
        String sql = "UPDATE tasks SET task_name = ?, description = ?, category = ?, priority = ? WHERE task_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, task.getTask_name());
            ps.setString(2, task.getTask_Description());
            ps.setString(3, task.getTask_Category());
            ps.setInt(4, task.getTask_Priority());
            ps.setInt(5, task.getTASKID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Appointment> getAppointments(String username) {
        ArrayList<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE username = ? ORDER BY appt_date, time_slot";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Appointment(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getDate("appt_date").toLocalDate(),
                        rs.getString("time_slot"),
                        rs.getString("location"),
                        rs.getString("machine_no"),
                        rs.getTimestamp("created_at").toLocalDateTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
