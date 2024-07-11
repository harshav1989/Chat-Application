package Mypackage;
import doryan.windowsnotificationapi.fr.Notification;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.TrayIcon;
import javax.swing.*;
import doryan.windowsnotificationapi.fr.Notification;
import java.awt.AWTException;
import java.net.MalformedURLException;

import java.util.List;
public class GroupChatPage extends JFrame {
    private JTextArea groupChatArea;
    private JTextField messageField;
    private JButton sendButton;
    private DatabaseHandler databaseHandler;
    private List<String> selectedUsers;
    private String currentUser; // Assuming you have a way to store the current logged-in user
    private final ChatServer chatServer;
    private JLabel statusLabel;
    private final ClientHandler clienthandler;

    public GroupChatPage(DatabaseHandler databaseHandler, List<String> selectedUsers, String currentUser , ChatServer chatserver , ClientHandler clienthandler) {
       this.databaseHandler = databaseHandler;
        this.selectedUsers = selectedUsers;
        this.currentUser = currentUser;
        this.chatServer = chatserver; // Assigning the ChatServer reference
        this.clienthandler=clienthandler;
        clienthandler.GPages.put(currentUser , this);
       // clienthandler.addclient(currentUser , clienthandler);

        try {
            if (!areAllUsersRegistered(selectedUsers)) {
                JOptionPane.showMessageDialog(null, "Some users in the group are not registered.");
                // Handle accordingly (e.g., redirect to register)
                dispose(); // Close the group chat window
                return;
            }
            setTitle("Group Chat -" + currentUser);
            setSize(400, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel();
            add(panel);
            placeComponents(panel);

            displayGroupChatHistory();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage());
            ex.printStackTrace(); // Log the exception
        }
        displayGroupChatHistory();
    }
    
public void placeComponents(JPanel panel) {
    panel.setLayout(new BorderLayout());

    JPanel topPanel = new JPanel();
    topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

    JButton backButton = new JButton("Back");
    backButton.addActionListener(e -> {
        dispose();
       });
    topPanel.add(backButton);
    
      statusLabel = new JLabel("Status: " + "online");
            add(statusLabel, BorderLayout.NORTH);

    panel.add(topPanel, BorderLayout.NORTH);

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(null);

    groupChatArea = new JTextArea();
    groupChatArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(groupChatArea);
    scrollPane.setBounds(10, 10, 360, 300);
    centerPanel.add(scrollPane);

    messageField = new JTextField();
    messageField.setBounds(10, 320, 260, 30);
    centerPanel.add(messageField);

    sendButton = new JButton("Send");
    sendButton.setBounds(280, 320, 90, 30);
    sendButton.addActionListener(e -> sendMessage());
    centerPanel.add(sendButton);

    panel.add(centerPanel, BorderLayout.CENTER);
}


    public void displayGroupChatHistory() {
        try {
            StringBuilder chatHistory = new StringBuilder();

            for (String user : selectedUsers) {
                List<String> userChatHistory = databaseHandler.getChatHistory("Group", user);
                if (userChatHistory != null && !userChatHistory.isEmpty()) {
                    chatHistory.append("Chat history with ").append(user).append(":\n");
                    for (String message : userChatHistory) {
                        chatHistory.append(message).append("\n");
                    }
                    chatHistory.append("\n");
                }
            }

            groupChatArea.setText(chatHistory.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error displaying chat history: " + ex.getMessage());
            ex.printStackTrace(); // Log the exception
        }
    }
    
    public void updateChatUI(String receivedMessage) {
        // Update the chat area or UI elements with the received message
        // For example, appending the received message to the chat area
        groupChatArea.append(receivedMessage + "\n");
    }

    
    public void createGroupChat() {
        try {
            if (!selectedUsers.isEmpty()) {
                // Create a group chat with the selected users
                chatServer.createGroupChat(selectedUsers);
                chatServer.sendGroupMessage(currentUser , selectedUsers , currentUser +"has joined!");
                Notification.sendNotification( currentUser + "has joined", "", TrayIcon.MessageType.NONE);
      
            
            // Optionally, update the user status for the current user
            boolean status = chatServer.getUserStatus(currentUser);
            chatServer.updateUserStatus(currentUser, status);
        }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error creating group chat: " + ex.getMessage());
            ex.printStackTrace(); // Log the exception
        }
    }

    public void sendMessage() {
        try {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {         
                appendToGroupChatArea("You: " + message + "\n");
                messageField.setText("");
                chatServer.sendGroupMessage( currentUser , selectedUsers , message);
                databaseHandler.saveChatHistory("Group", currentUser, message, currentUser);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error sending message: " + ex.getMessage());
            ex.printStackTrace(); // Log the exception
        }
    }

    private void appendToGroupChatArea(String message) {
        groupChatArea.append(message);
    }
    
    private boolean areAllUsersRegistered(List<String> users) {
        for (String user : users) {
            if (!isUserAuthenticated(user)) {
                return false; // Return false if any user is not registered
            }
        }
        return true; // Return true if all users are registered
    }

  private boolean isUserAuthenticated(String username) {
    // Implement logic to check if the user exists based on the username
    // Query the database to find if the username exists in the users table
    boolean userExists = databaseHandler.isUserExists(username); // Assuming a method to check user existence

    return userExists;
}
 

}
