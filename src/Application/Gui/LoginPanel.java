package Application.Gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Base.Login;
import Base.Person;
import Base.Resident;
import Base.Staff;

public class LoginPanel extends JPanel {
    private final MainWindow window;
    private final JTextField usernameField = new JTextField(22);
    private final JPasswordField passwordField = new JPasswordField(22);

    public LoginPanel(MainWindow window) {
        this.window = window;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Username:"), gbc);
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        add(usernameField, gbc);
        gbc.gridy = 1;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        JButton loginBtn = new JButton("Log in");
        loginBtn.addActionListener(e -> attemptLogin());
        JButton createBtn = new JButton("Create account");
        createBtn.addActionListener(e -> showCreateAccountDialog());
        buttons.add(loginBtn);
        buttons.add(createBtn);
        add(buttons, gbc);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        Login login = new Login(window.getDatabase());
        Person user = login.login(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        window.onLoginSuccess(user);
    }

    private void showCreateAccountDialog() {
        JTextField newUsername = new JTextField(16);
        JTextField newName = new JTextField(16);
        JTextField newEmail = new JTextField(16);
        JPasswordField newPassword = new JPasswordField(16);
        JPasswordField confirmPassword = new JPasswordField(16);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Resident", "Staff"});
        JTextField roomField = new JTextField(10);
        JLabel roomLabel = new JLabel("Room number:");

        // Show/hide room field depending on role
        roleCombo.addActionListener(e -> {
            boolean isResident = "Resident".equals(roleCombo.getSelectedItem());
            roomLabel.setVisible(isResident);
            roomField.setVisible(isResident);
        });

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        int row = 0;

        String[] labels = {"Username:", "Full name:", "Email:", "Password:", "Confirm password:", "Role:"};
        java.awt.Component[] fields = {newUsername, newName, newEmail, newPassword, confirmPassword, roleCombo};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill = GridBagConstraints.NONE;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.fill = GridBagConstraints.HORIZONTAL;
            form.add(fields[i], gbc);
            row++;
        }

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill = GridBagConstraints.NONE;
        form.add(roomLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(roomField, gbc);

        int result = JOptionPane.showConfirmDialog(this, form, "Create account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String u = newUsername.getText().trim();
        String n = newName.getText().trim();
        String em = newEmail.getText().trim();
        String pw = new String(newPassword.getPassword());
        String cpw = new String(confirmPassword.getPassword());
        String role = (String) roleCombo.getSelectedItem();
        String room = roomField.getText().trim();

        if (u.isEmpty() || n.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, name, and password are required.", "Create account",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!pw.equals(cpw)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Create account",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if ("Resident".equals(role) && room.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room number is required for residents.", "Create account",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Person person = "Resident".equals(role)
                ? new Resident(n, em, u, pw, room)
                : new Staff(n, em, u, pw);

        window.getDatabase().addItem(person);
        JOptionPane.showMessageDialog(this, "Account created! You can now log in.", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
