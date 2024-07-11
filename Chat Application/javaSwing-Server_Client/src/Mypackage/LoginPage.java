
package Mypackage;

import Mypackage.ChatClient.ChatAccess;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private LoginButtonActionListener loginButtonActionListener;
    private final DatabaseHandler databaseHandler;  
    private final ChatServer chatServer;
    public LoginPage(DatabaseHandler databaseHandler , ChatServer chatServer ) {
        this.databaseHandler = databaseHandler;
        this.chatServer = chatServer;
      
        setTitle("Login Page");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 160, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 160, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(100, 80, 80, 25);
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                performLogin(username, password , chatServer );
            }
        });
    }

   private void performLogin(String username, String password , ChatServer chatServer) {
    if (databaseHandler.verifyLogin(username, password)) {
        int port = 5555; // Set your desired port number  
        ClientHandler client1 = new ClientHandler(port, username, chatServer);
        new Thread(client1).start();
        chatServer.adduser(client1 , username);
     //   client1.addclient(username, client1);
        // Open UserListPage after successful login
        UserListPage userListPage = new UserListPage(databaseHandler, username, chatServer, client1);
        userListPage.setVisible(true);
        dispose(); // Close the login page after successful login
    } else {
        JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
    }
}
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setLoginButtonActionListener(LoginButtonActionListener listener) {
        this.loginButtonActionListener = listener;
        loginButton.addActionListener(e -> {
            if (loginButtonActionListener != null) {
                loginButtonActionListener.onLogin();
            }
        });
    }

    public interface LoginButtonActionListener {
        void onLogin();
    }
}
