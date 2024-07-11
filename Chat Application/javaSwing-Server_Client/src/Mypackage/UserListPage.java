package Mypackage;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserListPage extends JFrame {
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private final DatabaseHandler databaseHandler;
    private final String loggedInUser;
    private JLabel statusLabel;
    private JButton groupChatButton;
    private JButton openChatButton;
    private final ChatServer chatServer;
    private final ClientHandler clientHandler;

public UserListPage(DatabaseHandler databaseHandler, String loggedInUser,
                        ChatServer chatServer, ClientHandler clientHandler) {
        this.databaseHandler = databaseHandler;
        this.loggedInUser = loggedInUser;
        this.chatServer = chatServer;
        this.clientHandler = clientHandler;

        initializeUI();
        displayUserList();
        initializeButtons();
    }

 private void initializeButtons() {
    JPanel buttonPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;

    openChatButton = new JButton("Open Chat");
    buttonPanel.add(openChatButton, gbc);

    gbc.gridx = 1;
    groupChatButton = new JButton("Group Chat");
    buttonPanel.add(groupChatButton, gbc);

    openChatButton.addActionListener(e -> {
        String selectedUser = userList.getSelectedValue();
        if (selectedUser != null) {
            openChatPage(selectedUser);
        }
    });
    
    groupChatButton.addActionListener(e -> {
    // Obtain usernames for group chat via JOptionPane input
    //String userInput = JOptionPane.showInputDialog(null, "Enter usernames for group chat (comma-separated):");
    //if (userInput != null && !userInput.isEmpty()) {
        //List<String> selectedUsers = Arrays.asList(userInput.split(",")); // Extract usernames from input
        
        List<String> selectedUsers = new ArrayList<>();
        ListModel<String> model = userList.getModel();

        for (int i = 0; i < model.getSize(); i++) {
        String element = model.getElementAt(i);
        selectedUsers.add(element);
        }

        // Create and display the GroupChatPage
        GroupChatPage groupChatPage = new GroupChatPage(databaseHandler, selectedUsers, loggedInUser, chatServer, clientHandler);
        groupChatPage.displayGroupChatHistory();
        groupChatPage.setVisible(true);
        // Example: Creating a group chat
        groupChatPage.createGroupChat();
        // Example: Sending a message in the group chat
           // }
        });

    add(buttonPanel, BorderLayout.NORTH);
    add(new JScrollPane(userList), BorderLayout.CENTER);
}

  private void openChatPage(String selectedUser) {
    ChatPage chatPage = new ChatPage(selectedUser, clientHandler, databaseHandler, loggedInUser, chatServer);
    //clientHandler.sendMessage(selectedUser, message , loggedInUser);
    
    chatPage.setVisible(true);

    chatPage.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            // Handle chat closure on window close
            clientHandler.closeChat(selectedUser);
        }
    });
}

   private void initializeUI() {
        setTitle("client "+ loggedInUser);
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new BorderLayout());

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane scrollPane = new JScrollPane(userList);
        panel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel("Status:");
        panel.add(statusLabel, BorderLayout.SOUTH);

        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = userList.getSelectedValue();
               /* if (selectedUser != null) {
                    openChatPage(selectedUser);
                }
                */
            }
        });
    }


    private void displayUserList() {
        // Fetch users from the database handler and populate the list model
        List<String> users = databaseHandler.getUsers();
        for (String user : users) {
            if (!user.equals(loggedInUser)) {
                userListModel.addElement(user);
            }
        }

        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
       /* userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = userList.getSelectedValue();
                if (selectedUser != null) {
                    openChatPage(selectedUser);
                }
            }
        });*/
    }
    public void setGroupChatButtonActionListener(Runnable action) {
        // Check if the groupChatButton is initialized and set the action listener
        if (groupChatButton != null) {
            groupChatButton.addActionListener(e -> action.run());
        } else {
            // Handle the case where the button is not initialized
            System.out.println("Group chat button not initialized.");
        }
    }


    private void notifyUserSelected(String selectedUser) {
        // Pass the selected user and chat access instance to the listener
        if (userSelectionListener != null) {
            userSelectionListener.onUserSelected(selectedUser);
        }
    }

    public void updateStatusLabel(String status) {
        statusLabel.setText(status);
    }

    public void sendNotificationToUser(String user, String message) {
        // Your logic to send a notification to the specified user
        // For demonstration purposes, using JOptionPane to display a message dialog
        JOptionPane.showMessageDialog(null, "Notification for " + user + ": " + message);
    }

    private UserSelectionListener userSelectionListener;

  interface UserSelectionListener {
    void onUserSelected(String selectedUser);
}
    public void setUserSelectionListener(UserSelectionListener listener) {
        this.userSelectionListener = listener;
    }
    
    public List<String> getSelectedUsers() {
    List<String> selectedUsers = new ArrayList<>();
    int[] selectedIndices = userList.getSelectedIndices();

    for (int index : selectedIndices) {
        selectedUsers.add(userListModel.getElementAt(index));
    }

    return selectedUsers;
}

}
