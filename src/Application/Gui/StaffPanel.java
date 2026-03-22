package Application.Gui;

import java.awt.BorderLayout;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import Application.Notification.EmailData;
import Application.Notification.Notification;
import Base.Person;
import Base.Resident;
import Base.Staff;
import Business.Task;
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

    public StaffPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 16f));
        add(welcomeLabel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Residents", buildResidentsTab());
        tabs.addTab("Tasks", buildTasksTab());

        add(tabs, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> window.logout());
        south.add(logout);
        add(south, BorderLayout.SOUTH);
    }

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
                    setText(t.getTask_name() + " — " + t.getRoomNum() + " (" + status + ")");
                }
                return this;
            }
        });
        sortCombo.addActionListener(e -> refreshTaskList());
        JButton open = new JButton("Open");
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

    public void onShow() {
        Person u = window.getCurrentUser();
        welcomeLabel.setText("Welcome, " + u.getName() + " (Staff)");
        refreshResidents();
        refreshTaskList();
    }

    private void refreshResidents() {
        StringBuilder sb = new StringBuilder();
        for (Person p : window.getDatabase().getUsers()) {
            if (p instanceof Resident) {
                sb.append(p.toString()).append("\n");
            }
        }
        if (sb.length() == 0) {
            sb.append("No residents.");
        }
        residentsArea.setText(sb.toString());
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
        for (Task t : tasks) {
            taskListModel.addElement(t);
        }
    }

    private void openSelectedTask() {
        Task task = taskList.getSelectedValue();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.", "Tasks", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner, task.getTask_name(), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(8, 8));
        JTextArea info = new JTextArea(task.toString() + "\nComplete: " + task.isComplete());
        info.setEditable(false);
        info.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        dlg.add(new JScrollPane(info), BorderLayout.CENTER);

        ArrayList<Staff> staffMembers = new ArrayList<>();
        for (Person p : window.getDatabase().getUsers()) {
            if (p instanceof Staff) {
                staffMembers.add((Staff) p);
            }
        }
        JComboBox<Staff> assignCombo = new JComboBox<>(staffMembers.toArray(new Staff[0]));
        JPanel assignRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        assignRow.add(new JLabel("Assign to:"));
        assignRow.add(assignCombo);

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(assignRow);
        south.add(Box.createVerticalStrut(6));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton complete = new JButton("Mark complete");
        complete.setEnabled(!task.isComplete());
        complete.addActionListener(e -> {
            markComplete(task);
            dlg.dispose();
        });
        JButton assign = new JButton("Assign");
        assign.addActionListener(e -> {
            Staff s = (Staff) assignCombo.getSelectedItem();
            if (s == null) {
                return;
            }
            assignTask(task, s);
            dlg.dispose();
        });
        JButton close = new JButton("Close");
        close.addActionListener(e -> dlg.dispose());
        buttons.add(complete);
        buttons.add(assign);
        buttons.add(close);
        south.add(buttons);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        refreshTaskList();
    }

    private void markComplete(Task task) {
        task.TaskComplete();
        DatabaseManager db = window.getDatabase();
        db.reloadTasks(window.getTaskManager().GetTasks());

        EmailData emailData = new EmailData();
        emailData.setRecipient(task.reporter);
        emailData.setSubject("Task completed: " + task.getTask_name());
        emailData.setText("The task " + task.getTask_name() + " has been completed.");
        Notification notification = new Notification(emailData);
        notification.send();
        db.addItem(notification);

        JOptionPane.showMessageDialog(this, "Task marked complete.", "Tasks", JOptionPane.INFORMATION_MESSAGE);
    }

    private void assignTask(Task task, Staff staff) {
        task.Assign(staff);
        DatabaseManager db = window.getDatabase();

        EmailData emailData = new EmailData();
        emailData.setRecipient(staff);
        emailData.setSubject("Task assigned: " + task.getTask_name());
        emailData.setText("You have been assigned the task " + task.getTask_name());
        Notification notification = new Notification(emailData);
        notification.send();
        db.addItem(notification);

        EmailData emailData2 = new EmailData();
        emailData2.setRecipient(task.reporter);
        emailData2.setSubject("Task assigned: " + task.getTask_name());
        emailData2.setText("The task " + task.getTask_name() + " has been assigned to " + staff.getName());
        Notification notification2 = new Notification(emailData2);
        notification2.send();
        db.addItem(notification2);

        db.reloadTasks(window.getTaskManager().GetTasks());
        JOptionPane.showMessageDialog(this, "Task assigned to " + staff.getName() + ".", "Tasks",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCreateResidentDialog() {
        JTextField username = new JTextField(16);
        JTextField name = new JTextField(16);
        JTextField email = new JTextField(16);
        JPasswordField password = new JPasswordField(16);
        JTextField room = new JTextField(8);
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        form.add(username, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        form.add(name, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        form.add(email, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        form.add(password, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Room:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        form.add(room, gbc);

        int result = JOptionPane.showConfirmDialog(this, form, "Create resident", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
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
        emailData.setText("Your account has been created. You can now log in and change your password");
        Notification notification = new Notification(emailData);
        notification.send();
        window.getDatabase().addItem(notification);

        JOptionPane.showMessageDialog(this, "Resident created.", "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshResidents();
    }
}
