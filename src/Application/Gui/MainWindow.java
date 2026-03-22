package Application.Gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import Base.Person;
import Base.Staff;
import Business.Task_Manager;
import Data.DatabaseManager;

public class MainWindow extends JFrame {
    public static final String CARD_LOGIN = "LOGIN";
    public static final String CARD_RESIDENT = "RESIDENT";
    public static final String CARD_STAFF = "STAFF";

    private final DatabaseManager database;
    private final Task_Manager taskManager;
    private Person currentUser;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final LoginPanel loginPanel;
    private final ResidentPanel residentPanel;
    private final StaffPanel staffPanel;

    public MainWindow() {
        database = new DatabaseManager();
        taskManager = new Task_Manager();
        database.getTasks().forEach(taskManager::addTask);

        setTitle("GAH Facilities");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(920, 640);
        setLocationRelativeTo(null);

        loginPanel = new LoginPanel(this);
        residentPanel = new ResidentPanel(this);
        staffPanel = new StaffPanel(this);

        cardPanel.add(loginPanel, CARD_LOGIN);
        cardPanel.add(residentPanel, CARD_RESIDENT);
        cardPanel.add(staffPanel, CARD_STAFF);

        add(cardPanel, BorderLayout.CENTER);
        showCard(CARD_LOGIN);
    }

    public void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    public void onLoginSuccess(Person user) {
        this.currentUser = user;
        if (user instanceof Staff) {
            staffPanel.onShow();
            showCard(CARD_STAFF);
        } else {
            residentPanel.onShow();
            showCard(CARD_RESIDENT);
        }
    }

    public void logout() {
        currentUser = null;
        loginPanel.reset();
        showCard(CARD_LOGIN);
    }

    public DatabaseManager getDatabase() {
        return database;
    }

    public Task_Manager getTaskManager() {
        return taskManager;
    }

    public Person getCurrentUser() {
        return currentUser;
    }
}
