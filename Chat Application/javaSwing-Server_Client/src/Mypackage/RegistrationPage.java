
package Mypackage;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationPage extends JFrame {
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private DatabaseHandler databaseHandler;
    private final ChatServer chatServer;

    public RegistrationPage(DatabaseHandler databaseHandler , ChatServer chatserver) {
        this.databaseHandler = databaseHandler;
        this.chatServer = chatserver;
        setTitle("Registration Page");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 20, 80, 25);
        panel.add(nameLabel);

        nameField = new JTextField(20);
        nameField.setBounds(100, 20, 160, 25);
        panel.add(nameField);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 50, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 50, 160, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 80, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 80, 160, 25);
        panel.add(passwordField);

        registerButton = new JButton("Register");
        registerButton.setBounds(100, 120, 100, 25);
        panel.add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                performRegistration(name, username, password);
            }
        });
    }

    private void performRegistration(String name, String username, String password) {
        boolean registered = databaseHandler.registerUser(name, username, password);
        if (registered) {
            JOptionPane.showMessageDialog(null, "Registration successful!");
            LoginPage loginPage = new LoginPage(databaseHandler , chatServer);
            loginPage.setVisible(true);
            dispose(); // Close the registration page after successful registration
        } else {
            JOptionPane.showMessageDialog(null, "Failed to register user. Please try again.");
        }
    }

    public String getPassword() {
        // Assuming you have a password field in your RegistrationPage
        return new String(passwordField.getPassword());
    }

    public String getUsername() {
        // Assuming you have a username field in your RegistrationPage
        return usernameField.getText();
    }
    
    public void setRegisterButtonActionListener(Runnable action) {
        // Implement the action listener for the register button
        // For example:
        registerButton.addActionListener(e -> action.run());
    }
}

