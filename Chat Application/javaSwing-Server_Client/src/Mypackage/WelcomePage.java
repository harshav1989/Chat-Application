
package Mypackage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePage extends JFrame {
    private JButton loginButton;
    private JButton registerButton;
    private DatabaseHandler databaseHandler;
    private static String user;
    private final ChatServer chatserver;

    public WelcomePage(DatabaseHandler databaseHandler , ChatServer chatServer ) {
        this.databaseHandler = databaseHandler;
        this.chatserver = chatServer;
        setTitle("Chat Application");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel headingLabel = new JLabel("Chat Application");
        headingLabel.setBounds(70, 20, 160, 25);
        panel.add(headingLabel);

        loginButton = new JButton("Login");
        loginButton.setBounds(50, 70, 80, 25);
        panel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setBounds(150, 70, 100, 25);
        panel.add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginPage();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegistrationPage();
            }
        });
    }

   private void openLoginPage() {
       LoginPage loginPage = new LoginPage(databaseHandler , chatserver ); // Pass ChatAccess to LoginPage
    loginPage.setVisible(true);
    dispose(); // Close the welcome page after opening the login page
}

    private void openRegistrationPage() {
        RegistrationPage registrationPage = new RegistrationPage(databaseHandler , chatserver);
        registrationPage.setVisible(true);
        dispose(); // Close the welcome page after opening the registration page
    }
    
  void setLoginButtonActionListener(Runnable runnable) {
    loginButton.addActionListener(e -> runnable.run());
}

public void setRegisterButtonActionListener(Runnable action) {
    registerButton.addActionListener(e -> action.run());
}

}