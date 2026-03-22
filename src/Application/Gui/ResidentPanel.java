package Application.Gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;

import Application.Notification.EmailData;
import Application.Notification.Notification;
import Base.Person;
import Base.Resident;
import Business.Task;
import Business.Task_Manager;
import Data.Appointment;
import Data.DatabaseManager;

public class ResidentPanel extends JPanel {

    private static final String HOME = "HOME";
    private static final String SECURITY = "SECURITY";
    private static final String FACILITY = "FACILITY";
    private static final String WASH = "WASH";
    private static final String VIEW = "VIEW";
    private static final String NOTIFICATIONS = "NOTIFICATIONS";
    private static final String ACCOUNT = "ACCOUNT";

    private final MainWindow window;
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);
    private final JLabel welcomeLabel = new JLabel(" ");

    public ResidentPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());

        cardPanel.add(buildHome(), HOME);
        cardPanel.add(buildReportPanel("Security"), SECURITY);
        cardPanel.add(buildReportPanel("Facility"), FACILITY);
        cardPanel.add(buildWashPanel(), WASH);
        cardPanel.add(buildViewPanel(), VIEW);
        cardPanel.add(buildNotificationsPanel(), NOTIFICATIONS);
        cardPanel.add(buildAccountPanel(), ACCOUNT);

        add(cardPanel, BorderLayout.CENTER);
    }

    public void onShow() {
        welcomeLabel.setText("Welcome, " + window.getCurrentUser().getName() + "!");
        cards.show(cardPanel, HOME);
    }

    // ── Home ─────────────────────────────────────────────────────────────────

    private JPanel buildHome() {
        JPanel outer = new JPanel(new BorderLayout(0, 0));
        outer.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));

        // Header row
        JPanel header = new JPanel(new BorderLayout());
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 18f));
        header.add(welcomeLabel, BorderLayout.WEST);
        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton notifBtn = new JButton("Notifications");
        notifBtn.addActionListener(e -> cards.show(cardPanel, NOTIFICATIONS));
        JButton accountBtn = new JButton("Account");
        accountBtn.addActionListener(e -> cards.show(cardPanel, ACCOUNT));
        JButton logoutBtn = new JButton("Log out");
        logoutBtn.addActionListener(e -> window.logout());
        headerButtons.add(notifBtn);
        headerButtons.add(accountBtn);
        headerButtons.add(logoutBtn);
        header.add(headerButtons, BorderLayout.EAST);
        outer.add(header, BorderLayout.NORTH);

        // Main action buttons
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        String[] labels = {
            "Report Security Issue",
            "Report Facility Issue",
            "Book Wash Appointment",
            "View Issues & Appointments"
        };
        String[] destinations = { SECURITY, FACILITY, WASH, VIEW };

        for (int i = 0; i < labels.length; i++) {
            final String dest = destinations[i];
            JButton btn = new JButton(labels[i]);
            btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 15f));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.addActionListener(e -> cards.show(cardPanel, dest));
            center.add(btn);
            if (i < labels.length - 1) center.add(Box.createVerticalStrut(14));
        }

        outer.add(center, BorderLayout.CENTER);
        return outer;
    }

    // ── Report Issue (Security / Facility) ───────────────────────────────────

    private JPanel buildReportPanel(String category) {
        JTextField nameField = new JTextField();
        JTextArea descArea = new JTextArea(4, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        form.add(new JLabel("Issue title:"), gbc);
        gbc.gridy = 1; form.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(nameField, gbc);
        gbc.gridy = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        form.add(new JScrollPane(descArea), gbc);
        gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weighty = 0;
        gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton submit = new JButton("Submit " + category + " Report");
        submit.addActionListener(e -> {
            String name = nameField.getText().trim();
            String desc = descArea.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(form, "Enter a title for the issue.", "Report", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Person user = window.getCurrentUser();
            Resident r = (Resident) user;
            Task task = new Task(name, desc, category, r.getRoomNumber());
            task.reporter = r;
            window.getTaskManager().addTask(task);

            EmailData emailData = new EmailData();
            emailData.setRecipient(user);
            emailData.setSubject(category + " issue reported: " + name);
            emailData.setText("Your " + category.toLowerCase() + " issue \"" + name + "\" has been submitted.");
            Notification notification = new Notification(emailData);
            notification.send();
            window.getDatabase().addItem(notification);
            window.getDatabase().addItem(task);

            nameField.setText("");
            descArea.setText("");
            JOptionPane.showMessageDialog(form, "Issue submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            cards.show(cardPanel, HOME);
        });
        form.add(submit, gbc);

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(buildBackBar("Report " + category + " Issue"), BorderLayout.NORTH);
        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    // ── Book Wash Appointment ────────────────────────────────────────────────

    private JPanel buildWashPanel() {
        String[] slots = {
            "7:00 AM – 9:00 AM",
            "9:00 AM – 11:00 AM",
            "11:00 AM – 1:00 PM",
            "1:00 PM – 3:00 PM",
            "3:00 PM – 5:00 PM",
            "5:00 PM – 7:00 PM"
        };
        JComboBox<String> slotCombo = new JComboBox<>(slots);
        JComboBox<String> locationCombo = new JComboBox<>(new String[]{"Block J Laundry Room", "Block G Laundry Room"});
        JTextField machineField = new JTextField(10);

        SpinnerDateModel dateModel = new SpinnerDateModel(
                new Date(), new Date(), null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(dateSpinner, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        form.add(new JLabel("Time slot:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(slotCombo, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        form.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(locationCombo, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        form.add(new JLabel("Machine number:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(machineField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        JButton book = new JButton("Book Appointment");
        book.addActionListener(e -> {
            String machine = machineField.getText().trim();
            if (machine.isEmpty()) {
                JOptionPane.showMessageDialog(form, "Enter a machine number.", "Book Appointment", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Date selectedDate = (Date) dateSpinner.getValue();
            LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String slot = (String) slotCombo.getSelectedItem();
            String location = (String) locationCombo.getSelectedItem();
            String username = window.getCurrentUser().getUsername();

            Appointment appt = new Appointment(username, localDate, slot, location, machine);
            window.getDatabase().addAppointment(appt);

            machineField.setText("");
            JOptionPane.showMessageDialog(form, "Appointment booked for " + localDate + ", " + slot + ".", "Booked", JOptionPane.INFORMATION_MESSAGE);
            cards.show(cardPanel, HOME);
        });
        form.add(book, gbc);

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(buildBackBar("Book Wash Appointment"), BorderLayout.NORTH);
        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    // ── View Issues & Appointments ────────────────────────────────────────────

    private JPanel buildViewPanel() {
        DefaultListModel<Task> taskModel = new DefaultListModel<>();
        JList<Task> taskList = new JList<>(taskModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Task) {
                    Task t = (Task) value;
                    String cat = t.getTask_Category();
                    String status = t.isComplete() ? "Complete" : "Open";
                    setText("[" + cat + "] " + t.getTask_name() + " — " + status);
                }
                return this;
            }
        });

        DefaultListModel<Appointment> apptModel = new DefaultListModel<>();
        JList<Appointment> apptList = new JList<>(apptModel);
        apptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            refreshTaskList(taskModel);
            refreshApptList(apptModel);
        });

        JButton openTaskBtn = new JButton("View / Recant task");
        openTaskBtn.addActionListener(e -> openTask(taskList, taskModel));

        JPanel issuesPanel = new JPanel(new BorderLayout(0, 4));
        issuesPanel.setBorder(BorderFactory.createTitledBorder("My Issues"));
        JPanel issuesBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        issuesBtns.add(refreshBtn);
        issuesBtns.add(openTaskBtn);
        issuesPanel.add(issuesBtns, BorderLayout.NORTH);
        issuesPanel.add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel apptsPanel = new JPanel(new BorderLayout(0, 4));
        apptsPanel.setBorder(BorderFactory.createTitledBorder("My Wash Appointments"));
        apptsPanel.add(new JScrollPane(apptList), BorderLayout.CENTER);

        JPanel body = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 0.6; gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(8, 8, 4, 8);
        body.add(issuesPanel, gbc);
        gbc.weighty = 0.4; gbc.gridy = 1; gbc.insets = new Insets(4, 8, 8, 8);
        body.add(apptsPanel, gbc);

        JPanel outer = new JPanel(new BorderLayout());
        JPanel backBar = buildBackBar("My Issues & Appointments");
        // Attach a show-listener by overriding addNotify on the outer panel
        outer.add(backBar, BorderLayout.NORTH);
        outer.add(body, BorderLayout.CENTER);

        // Refresh when this panel becomes visible via the back bar refresh button
        JButton viewRefresh = new JButton("Refresh");
        viewRefresh.addActionListener(e -> {
            refreshTaskList(taskModel);
            refreshApptList(apptModel);
        });
        ((JPanel) backBar.getComponent(1)).add(viewRefresh, 0);

        return outer;
    }

    private void refreshTaskList(DefaultListModel<Task> model) {
        model.clear();
        Person user = window.getCurrentUser();
        for (Task t : window.getTaskManager().GetTasks()) {
            if (t.reporter != null && t.reporter.getUsername().equals(user.getUsername())) {
                model.addElement(t);
            }
        }
    }

    private void refreshApptList(DefaultListModel<Appointment> model) {
        model.clear();
        String username = window.getCurrentUser().getUsername();
        for (Appointment a : window.getDatabase().getAppointments(username)) {
            model.addElement(a);
        }
    }

    private void openTask(JList<Task> taskList, DefaultListModel<Task> taskModel) {
        Task task = taskList.getSelectedValue();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.", "Tasks", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner, task.getTask_name(), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(8, 8));
        JTextArea info = new JTextArea(task.toString());
        info.setEditable(false);
        info.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        dlg.add(new JScrollPane(info), BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton recant = new JButton("Recant");
        recant.setEnabled(!task.isComplete());
        recant.addActionListener(e -> {
            Task_Manager tm = window.getTaskManager();
            DatabaseManager db = window.getDatabase();
            tm.removeTask(task);
            db.reloadTasks(tm.GetTasks());

            EmailData emailData = new EmailData();
            emailData.setRecipient(task.reporter);
            emailData.setSubject("Issue recanted: " + task.getTask_name());
            emailData.setText("Your issue \"" + task.getTask_name() + "\" has been recanted.");
            Notification notification = new Notification(emailData);
            notification.send();
            db.addItem(notification);

            dlg.dispose();
            refreshTaskList(taskModel);
            JOptionPane.showMessageDialog(this, "Issue recanted.", "Done", JOptionPane.INFORMATION_MESSAGE);
        });
        JButton close = new JButton("Close");
        close.addActionListener(e -> dlg.dispose());
        btns.add(recant);
        btns.add(close);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(400, 200));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ── Notifications ─────────────────────────────────────────────────────────

    private JPanel buildNotificationsPanel() {
        JTextArea area = new JTextArea(14, 40);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> {
            Person user = window.getCurrentUser();
            StringBuilder sb = new StringBuilder();
            for (Notification n : window.getDatabase().getNotifications()) {
                if (n.getRecipient() != null && n.getRecipient().getUsername().equals(user.getUsername())) {
                    sb.append("• ").append(n.getSubjectLine()).append("\n");
                }
            }
            area.setText(sb.length() == 0 ? "No notifications." : sb.toString());
            area.setCaretPosition(0);
        });

        JPanel body = new JPanel(new BorderLayout(0, 6));
        body.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        body.add(refresh, BorderLayout.NORTH);
        body.add(new JScrollPane(area), BorderLayout.CENTER);

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(buildBackBar("Notifications"), BorderLayout.NORTH);
        outer.add(body, BorderLayout.CENTER);
        return outer;
    }

    // ── Account ───────────────────────────────────────────────────────────────

    private JPanel buildAccountPanel() {
        JPasswordField current = new JPasswordField(15);
        JPasswordField newPwd = new JPasswordField(15);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Current password:"), gbc);
        gbc.gridy = 1; form.add(new JLabel("New password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(current, gbc);
        gbc.gridy = 1; form.add(newPwd, gbc);
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        JButton change = new JButton("Change password");
        change.addActionListener(e -> {
            Person user = window.getCurrentUser();
            String cur = new String(current.getPassword());
            String np = new String(newPwd.getPassword());
            if (!user.getPassword().equals(cur)) {
                JOptionPane.showMessageDialog(form, "Current password is incorrect.", "Account", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (np.isEmpty()) {
                JOptionPane.showMessageDialog(form, "Enter a new password.", "Account", JOptionPane.WARNING_MESSAGE);
                return;
            }
            user.setPassword(np);
            window.getDatabase().UpdateUser(user);
            current.setText(""); newPwd.setText("");
            JOptionPane.showMessageDialog(form, "Password changed.", "Account", JOptionPane.INFORMATION_MESSAGE);
            cards.show(cardPanel, HOME);
        });
        form.add(change, gbc);

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(buildBackBar("Account"), BorderLayout.NORTH);
        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    /** Builds a top bar with a title on the left and a Back button on the right. */
    private JPanel buildBackBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel lbl = new JLabel(title);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        bar.add(lbl, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JButton back = new JButton("Back");
        back.addActionListener(e -> cards.show(cardPanel, HOME));
        right.add(back);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }
}
