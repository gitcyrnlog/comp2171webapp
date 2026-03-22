package Application.Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import Application.Notification.EmailData;
import Application.Notification.Notification;
import Base.Person;
import Base.Resident;
import Base.Staff;
import Business.Task;
import Data.Appointment;
import Data.DatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StaffPanel extends JPanel {
    private final MainWindow window;
    private final JLabel welcomeLabel = new JLabel(" ");
    private final JTextArea residentsArea = new JTextArea(12, 44);
    private final JComboBox<String> sortCombo = new JComboBox<>(
            new String[] { "By name", "By room number", "By priority" });
    private final DefaultListModel<Task> taskListModel = new DefaultListModel<>();
    private final JList<Task> taskList = new JList<>(taskListModel);
    private final DefaultListModel<Appointment> apptListModel = new DefaultListModel<>();
    private final JList<Appointment> apptList = new JList<>(apptListModel);

    public StaffPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 16f));
        add(welcomeLabel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Residents", buildResidentsTab());
        tabs.addTab("Tasks", buildTasksTab());
        tabs.addTab("Appointments", buildAppointmentsTab());

        add(tabs, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> window.logout());
        south.add(logout);
        add(south, BorderLayout.SOUTH);
    }

    // ── Residents tab ─────────────────────────────────────────────────────────

    private JPanel buildResidentsTab() {
        JPanel p = new JPanel(new BorderLayout());
        residentsArea.setEditable(false);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshResidents());
        JButton create = new JButton("Create resident");
        create.addActionListener(e -> showCreateResidentDialog());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(refresh);
        top.add(create);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(residentsArea), BorderLayout.CENTER);
        return p;
    }

    // ── Tasks tab ─────────────────────────────────────────────────────────────

    private JPanel buildTasksTab() {
        JPanel p = new JPanel(new BorderLayout());
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Task) {
                    Task t = (Task) value;
                    String status = t.isComplete() ? "Complete" : "Open";
                    setText("[" + t.getTask_Category() + "] " + t.getTask_name()
                            + " — " + t.getRoomNum() + " (" + status + ")");
                }
                return this;
            }
        });
        sortCombo.addActionListener(e -> refreshTaskList());
        JButton open = new JButton("Open / Edit");
        open.addActionListener(e -> openSelectedTask());
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshTaskList());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Sort:"));
        top.add(sortCombo);
        top.add(refresh);
        top.add(open);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(taskList), BorderLayout.CENTER);
        return p;
    }

    // ── Appointments tab ──────────────────────────────────────────────────────

    private JPanel buildAppointmentsTab() {
        JPanel p = new JPanel(new BorderLayout());
        apptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        apptList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Appointment) {
                    Appointment a = (Appointment) value;
                    setText(a.getDate() + "  |  " + a.getLocation() + "  |  " + a.getTimeSlot()
                            + "  |  Machine " + a.getMachineNo() + "  — " + a.getUsername());
                }
                return this;
            }
        });
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshApptList());
        JButton delete = new JButton("Delete");
        delete.setForeground(Color.RED);
        delete.addActionListener(e -> deleteSelectedAppt());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(refresh);
        top.add(delete);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(apptList), BorderLayout.CENTER);
        return p;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    public void onShow() {
        Person u = window.getCurrentUser();
        welcomeLabel.setText("Welcome, " + u.getName() + " (Staff)");
        refreshResidents();
        refreshTaskList();
        refreshApptList();
    }

    private void refreshResidents() {
        StringBuilder sb = new StringBuilder();
        for (Person p : window.getDatabase().getUsers()) {
            if (p instanceof Resident) {
                sb.append(p.toString()).append("\n");
            }
        }
        residentsArea.setText(sb.length() == 0 ? "No residents." : sb.toString());
        residentsArea.setCaretPosition(0);
    }

    private void refreshTaskList() {
        taskListModel.clear();
        List<Task> tasks = new ArrayList<>(window.getTaskManager().GetTasks());
        String mode = (String) sortCombo.getSelectedItem();
        if ("By name".equals(mode)) {
            Collections.sort(tasks, (t1, t2) -> t1.getTask_name().compareTo(t2.getTask_name()));
        } else if ("By room number".equals(mode)) {
            Collections.sort(tasks, (t1, t2) -> t1.getRoomNum().compareTo(t2.getRoomNum()));
        } else {
            Collections.sort(tasks, (t1, t2) -> Integer.compare(t1.getTask_Priority(), t2.getTask_Priority()));
        }
        for (Task t : tasks) taskListModel.addElement(t);
    }

    private void refreshApptList() {
        apptListModel.clear();
        for (Appointment a : window.getDatabase().getAllAppointments()) {
            apptListModel.addElement(a);
        }
    }

    private void deleteSelectedAppt() {
        Appointment appt = apptList.getSelectedValue();
        if (appt == null) {
            JOptionPane.showMessageDialog(this, "Select an appointment first.", "Appointments",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete appointment for " + appt.getUsername() + " on " + appt.getDate() + "?",
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        window.getDatabase().deleteAppointment(appt.getId());
        refreshApptList();
        JOptionPane.showMessageDialog(this, "Appointment deleted.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Task dialog (open / edit / complete / assign) ─────────────────────────

    private void openSelectedTask() {
        Task task = taskList.getSelectedValue();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.", "Tasks", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner, task.getTask_name(), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(8, 8));

        // ── Info area
        JTextArea info = new JTextArea(task.toString() + "\nStatus: " + (task.isComplete() ? "Complete" : "Open"));
        info.setEditable(false);
        info.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        dlg.add(new JScrollPane(info), BorderLayout.CENTER);

        // ── South panel
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        // Custom message to include in email
        JPanel msgPanel = new JPanel(new BorderLayout(4, 0));
        msgPanel.setBorder(BorderFactory.createTitledBorder("Message to resident (included in email)"));
        JTextArea msgArea = new JTextArea(3, 30);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgPanel.add(new JScrollPane(msgArea), BorderLayout.CENTER);
        south.add(msgPanel);
        south.add(Box.createVerticalStrut(6));

        // Assign row
        ArrayList<Staff> staffMembers = new ArrayList<>();
        for (Person p : window.getDatabase().getUsers()) {
            if (p instanceof Staff) staffMembers.add((Staff) p);
        }
        JComboBox<Staff> assignCombo = new JComboBox<>(staffMembers.toArray(new Staff[0]));
        JPanel assignRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        assignRow.add(new JLabel("Assign to:"));
        assignRow.add(assignCombo);
        south.add(assignRow);
        south.add(Box.createVerticalStrut(4));

        // Action buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton complete = new JButton("Mark complete");
        complete.setEnabled(!task.isComplete());
        complete.addActionListener(e -> {
            markComplete(task, msgArea.getText().trim());
            dlg.dispose();
            refreshTaskList();
        });
        JButton assign = new JButton("Assign");
        assign.addActionListener(e -> {
            Staff s = (Staff) assignCombo.getSelectedItem();
            if (s == null) return;
            assignTask(task, s, msgArea.getText().trim());
            dlg.dispose();
            refreshTaskList();
        });
        JButton edit = new JButton("Edit task");
        edit.addActionListener(e -> {
            dlg.dispose();
            showEditTaskDialog(task);
        });
        JButton close = new JButton("Close");
        close.addActionListener(e -> dlg.dispose());
        buttons.add(complete);
        buttons.add(assign);
        buttons.add(edit);
        buttons.add(close);
        south.add(buttons);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setMinimumSize(new java.awt.Dimension(500, 300));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void showEditTaskDialog(Task task) {
        JTextField nameField = new JTextField(task.getTask_name(), 20);
        JTextArea descArea = new JTextArea(task.getTask_Description(), 4, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JTextField categoryField = new JTextField(task.getTask_Category(), 15);
        JSpinner prioritySpinner = new JSpinner(new SpinnerNumberModel(task.getTask_Priority(), 0, 10, 1));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(nameField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.FIRST_LINE_END; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        form.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        form.add(new JScrollPane(descArea), gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill = GridBagConstraints.NONE; gbc.weighty = 0;
        form.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(categoryField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Priority (0-10):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        form.add(prioritySpinner, gbc);

        int result = JOptionPane.showConfirmDialog(this, form, "Edit task: " + task.getTask_name(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String newName = nameField.getText().trim();
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Edit task", JOptionPane.WARNING_MESSAGE);
            return;
        }
        task.setTask_name(newName);
        task.setTask_Description(descArea.getText().trim());
        task.setCategory(categoryField.getText().trim());
        task.setTask_Priority((Integer) prioritySpinner.getValue());
        window.getDatabase().updateTask(task);
        refreshTaskList();
        JOptionPane.showMessageDialog(this, "Task updated.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Email actions ─────────────────────────────────────────────────────────

    private void markComplete(Task task, String staffMessage) {
        task.TaskComplete();
        DatabaseManager db = window.getDatabase();
        db.reloadTasks(window.getTaskManager().GetTasks());

        String body = "Your issue \"" + task.getTask_name() + "\" has been marked as complete.";
        if (!staffMessage.isEmpty()) body += "\n\nMessage from staff: " + staffMessage;

        EmailData emailData = new EmailData();
        emailData.setRecipient(task.reporter);
        emailData.setSubject("Issue resolved: " + task.getTask_name());
        emailData.setText(body);
        Notification notification = new Notification(emailData);
        notification.send();
        db.addItem(notification);

        JOptionPane.showMessageDialog(this, "Task marked complete and resident notified.", "Done",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void assignTask(Task task, Staff staff, String staffMessage) {
        task.Assign(staff);
        DatabaseManager db = window.getDatabase();

        // Notify the assigned staff member
        EmailData ed1 = new EmailData();
        ed1.setRecipient(staff);
        ed1.setSubject("Task assigned: " + task.getTask_name());
        ed1.setText("You have been assigned the task \"" + task.getTask_name() + "\".");
        Notification n1 = new Notification(ed1);
        n1.send();
        db.addItem(n1);

        // Notify the resident reporter
        String body = "Your issue \"" + task.getTask_name() + "\" has been assigned to " + staff.getName() + ".";
        if (!staffMessage.isEmpty()) body += "\n\nMessage from staff: " + staffMessage;
        EmailData ed2 = new EmailData();
        ed2.setRecipient(task.reporter);
        ed2.setSubject("Update on your issue: " + task.getTask_name());
        ed2.setText(body);
        Notification n2 = new Notification(ed2);
        n2.send();
        db.addItem(n2);

        db.reloadTasks(window.getTaskManager().GetTasks());
        JOptionPane.showMessageDialog(this, "Task assigned to " + staff.getName() + " and resident notified.", "Done",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Create resident dialog ─────────────────────────────────────────────────

    private void showCreateResidentDialog() {
        JTextField username = new JTextField(16);
        JTextField name = new JTextField(16);
        JTextField email = new JTextField(16);
        JPasswordField password = new JPasswordField(16);
        JTextField room = new JTextField(8);
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        String[] lbls = {"Username:", "Name:", "Email:", "Password:", "Room:"};
        Component[] flds = {username, name, email, password, room};
        for (int i = 0; i < lbls.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.anchor = GridBagConstraints.LINE_END;
            form.add(new JLabel(lbls[i]), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
            form.add(flds[i], gbc);
        }
        int result = JOptionPane.showConfirmDialog(this, form, "Create resident",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        String u = username.getText().trim();
        String n = name.getText().trim();
        String em = email.getText().trim();
        String pw = new String(password.getPassword());
        String rm = room.getText().trim();
        if (u.isEmpty() || n.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and name are required.", "Create resident",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Resident resident = new Resident(n, em, u, pw, rm);
        window.getDatabase().addItem(resident);

        EmailData emailData = new EmailData();
        emailData.setRecipient(resident);
        emailData.setSubject("Account created");
        emailData.setText("Your account has been created. You can now log in and change your password.");
        Notification notification = new Notification(emailData);
        notification.send();
        window.getDatabase().addItem(notification);

        JOptionPane.showMessageDialog(this, "Resident created.", "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshResidents();
    }
}
