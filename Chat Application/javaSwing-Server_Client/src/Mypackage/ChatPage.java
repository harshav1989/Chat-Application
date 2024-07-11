package Mypackage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.border.AbstractBorder;
import javax.swing.text.*;


class FileChooser {

    public static String chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath(); // Get the absolute path as a string
            return filePath;
        } else {
            System.out.println("File selection canceled by the user.");
            return null;
        }
    }
}

public class ChatPage extends JFrame {

    private String selectedUser;
    private JTextArea chatArea;
     private JTextPane chatarea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton sendMediaButton;
    private JButton backButton;
    private JButton exitButton;
    private JPanel inputPanel;
    private DatabaseHandler databaseHandler;
    private String loggedInUser;
    private ChatServer chatServer;
    private ClientHandler clientHandler;
    private JScrollPane scrollPane;
    private JPanel chatBubblePanel;

    public ChatPage(String selectedUser, ClientHandler clientHandler, DatabaseHandler dbHandler, String user, ChatServer server) {
        this.selectedUser = selectedUser;
        this.clientHandler = clientHandler;
        this.databaseHandler = dbHandler;
        this.loggedInUser = user;
        this.chatServer = server;
        initComponents();
        displayChatHistory();
        chatServer.adduser(clientHandler, user);
       // clientHandler.addclient(user, clientHandler);
        clientHandler.chatPages.put(user, this);

    }

private void initComponents() {
    setTitle("Chat with " + selectedUser);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(new Color(240, 240, 240)); // Light background color
    System.out.println("element added");

    JPanel userInfoPanel = new JPanel(new BorderLayout());
    userInfoPanel.setBackground(Color.WHITE);
    userInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
     System.out.println("element added");
    
    String iconPath = "C:\\Users\\MY PC\\Desktop\\java prj\\icon.png"; // Update with your icon path
    ImageIcon userIcon = new ImageIcon(iconPath);
    JLabel userIconLabel = new JLabel();
    userIconLabel.setIcon(new ImageIcon(userIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
    userIconLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    userIconLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    userIconLabel.setPreferredSize(new Dimension(60, 60));

    JPanel userDetailsPanel = new JPanel(new GridLayout(2, 1));
    userDetailsPanel.setBackground(Color.WHITE);
     System.out.println("element added");
     
    chatBubblePanel = new JPanel();
    chatBubblePanel.setLayout(new BoxLayout(chatBubblePanel, BoxLayout.Y_AXIS));


    JLabel usernameLabel = new JLabel(selectedUser);
    usernameLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Bold username
    userDetailsPanel.add(usernameLabel);
     System.out.println("element added");

    JLabel statusLabel = new JLabel("Online");
    statusLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Smaller font for status
    statusLabel.setForeground(Color.GREEN);
    userDetailsPanel.add(statusLabel);
    System.out.println("element added");

    userInfoPanel.add(userIconLabel, BorderLayout.WEST);
    userInfoPanel.add(userDetailsPanel, BorderLayout.CENTER);
    System.out.println("element added");

    mainPanel.add(userInfoPanel, BorderLayout.NORTH);

    JPanel chatPanel = new JPanel(new BorderLayout());
    chatPanel.setBackground(new Color(240, 240, 240)); // Light background color

    chatArea = new JTextArea();
    chatArea.setEditable(false);
    chatArea.setBackground(Color.WHITE);
    scrollPane = new JScrollPane(chatArea);
    chatPanel.add(scrollPane, BorderLayout.CENTER);
     System.out.println("element added");
  

     JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setBackground(new Color(240, 240, 240));

    messageField = new JTextField();
    messageField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    bottomPanel.add(messageField, BorderLayout.CENTER);
     System.out.println("element added");

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Add a new panel for buttons
    buttonPanel.setBackground(new Color(240, 240, 240));

    sendButton = new JButton("Send");
    sendButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    buttonPanel.add(sendButton);
     System.out.println("element added");

    sendMediaButton = new JButton("Send Media"); // Add the Send Media button
    sendMediaButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    buttonPanel.add(sendMediaButton);
     System.out.println("element added");

    backButton = new JButton("Back"); // Add the Back button
    backButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    buttonPanel.add(backButton);
     System.out.println("element added");

    exitButton = new JButton("Exit"); // Add the Exit button
    exitButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    buttonPanel.add(exitButton);
     System.out.println("element added");

    bottomPanel.add(buttonPanel, BorderLayout.EAST); // Align the button panel to the right
    bottomPanel.add(messageField, BorderLayout.CENTER);
    System.out.println("element added");

    chatPanel.add(bottomPanel, BorderLayout.SOUTH);
    mainPanel.add(chatPanel, BorderLayout.CENTER);
     mainPanel.add(userInfoPanel, BorderLayout.NORTH);
      System.out.println("element added");

         displayChatHistory();
       
        
        sendButton.addActionListener(e -> {
            // Send message logic modified to use ClientHandler's sendMessage method
            String message = messageField.getText();
            if (!message.isEmpty()) {
                // clientHandler.sendMessage(selectedUser, message , loggedInUser);
                chatServer.sendMessageToUser(loggedInUser, selectedUser, message);
                 displaySenderMessage(message); 
                 messageField.setText("");
                
            }
        });

        sendMediaButton.addActionListener(e -> sendMediaAction());

        backButton.addActionListener(e -> {
            handleBackButton();
            dispose();
        });

        exitButton.addActionListener(e -> {
            dispose();
        });
        
         add(mainPanel); // Assuming this JFrame is the main frame
    pack(); // Sizes the frame based on the added components
    setLocationRelativeTo(null); // Centers the frame on the screen
    setVisible(true); // Set visibility after adding all components

    }

  /*  private void displayChatHistory() {
        List<String> chatHistory = clientHandler.getChatHistory(selectedUser);
        for (String message : chatHistory) {
            appendToChatArea(message);
        }
    }
*/

private void displayChatHistory() {
    List<String> chatHistory = clientHandler.getChatHistory(selectedUser);

    for (String message : chatHistory) {
        if (message.startsWith(loggedInUser + ": ")) {
            displaySenderMessage(message.substring(loggedInUser.length() + 2)); // Adjust the substring index
        } else {
            JPanel receiverBubble = createBubblePanel(message, false);
    chatBubblePanel.add(receiverBubble);
    chatBubblePanel.add(Box.createVerticalStrut(5)); // Add spacing between bubbles
    scrollPane.getViewport().setView(chatBubblePanel); // Update the viewport
    chatBubblePanel.revalidate();
    chatBubblePanel.repaint();
        }
    }
}

    private void handleBackButton() {
        backButton.addActionListener(e -> {
            UserListPage userListPage = new UserListPage(databaseHandler, loggedInUser, chatServer, clientHandler);
            userListPage.setUserSelectionListener(selectedUser -> {
                // Logic to handle selected user in the user list page
                // For example, navigating back to the chat page for the selected user
                if (selectedUser != null) {
                    ChatPage chatPage = new ChatPage(selectedUser, clientHandler, databaseHandler, loggedInUser, chatServer);
                    chatPage.setVisible(true);
                }
            });
            userListPage.setVisible(true);
            dispose(); // Close the current chat page
        });
    }

    public void appendToChatArea(String message) {
       //  chatArea.append(message + "\n");  
         JPanel receiverBubble = createBubblePanel(message, false);
        chatArea.add(receiverBubble);
        chatArea.revalidate();
        chatArea.repaint();
    }
    
    private void sendMediaAction() {
        String selectedMediaFile = FileChooser.chooseFile();

        if (selectedMediaFile != null) {
            clientHandler.sendImage(loggedInUser,selectedUser, selectedMediaFile);
            System.out.println("Selected file: " + selectedMediaFile);

        }
    }
    
   private void displaySenderMessage(String message) {
    JPanel senderBubble = createBubblePanel(message, true);
    chatBubblePanel.add(senderBubble);
    chatBubblePanel.add(Box.createVerticalStrut(5)); // Add spacing between bubbles
    scrollPane.getViewport().setView(chatBubblePanel); // Update the viewport
    chatBubblePanel.revalidate();
    chatBubblePanel.repaint();
    }

    public void displayMessageOnUI(String message) {
        SwingUtilities.invokeLater(() -> {
           JPanel receiverBubble = createBubblePanel(message, false);
    chatBubblePanel.add(receiverBubble);
    chatBubblePanel.add(Box.createVerticalStrut(5)); // Add spacing between bubbles
    scrollPane.getViewport().setView(chatBubblePanel); // Update the viewport
    chatBubblePanel.revalidate();
    chatBubblePanel.repaint(); });
    }
    
private JPanel createBubblePanel(String message, boolean sender) {
    JPanel bubblePanel = new JPanel();
    bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.Y_AXIS));
    bubblePanel.setBorder(new RoundBorder(30)); // RoundBorder with radius 10

    JTextArea messageArea = new JTextArea(message);
    messageArea.setLineWrap(true);
    messageArea.setWrapStyleWord(true);
    messageArea.setEditable(false);
    messageArea.setOpaque(false); // Make text area transparent
    messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

    // Auto-size width and adjust height based on text content
    int width = 200; // Default width
    int height = (int) messageArea.getPreferredSize().getHeight();
    int maxWidth = 300; // Maximum width
    messageArea.setSize(new Dimension(width, height));
    if (messageArea.getPreferredSize().getWidth() > maxWidth) {
        width = maxWidth;
    } else {
        width = (int) messageArea.getPreferredSize().getWidth();
    }
    messageArea.setPreferredSize(new Dimension(width, (int) messageArea.getPreferredSize().getHeight()));

    bubblePanel.add(messageArea);


   if (sender) {
        bubblePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        bubblePanel.setBackground(new Color(0, 0, 255, 100)); // Blue color with transparency for sender
        messageArea.setBackground(new Color(0, 0, 255, 150)); // Blue color with transparency for text area
         message = addTimestamp(message);
    } else {
        bubblePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bubblePanel.setBackground(new Color(0, 255, 0, 100)); // Green color with transparency for receiver
        messageArea.setBackground(new Color(0, 255, 0, 150)); // Green color with transparency for text area      
        message = addTimestamp(message);
    }

    return bubblePanel;
}

    private String addTimestamp(String message) {
    return "[" + java.time.LocalTime.now() + "] " + message;
}
 
  public void displayReceivedImage(ImageIcon image) {
    if (image != null) {
        JLabel receivedImageLabel = new JLabel(image);

        // Create a panel for the image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(receivedImageLabel, BorderLayout.CENTER);

        // Get the existing viewport view
        Component existingView = scrollPane.getViewport().getView();

        // Create a new panel to hold both existing content and the image
        JPanel contentPanel = new JPanel(new BorderLayout());

        if (existingView != null) {
            // Add the existing content
            contentPanel.add(existingView, BorderLayout.NORTH);
        }

        // Add the image panel
        contentPanel.add(imagePanel, BorderLayout.CENTER);

        // Set the combined content as the viewport view
        scrollPane.setViewportView(contentPanel);

        scrollPane.revalidate();
        scrollPane.repaint();    
        }  }
    
    // Define the SendMessageListener interface
    public interface SendMessageListener {

        void sendMessage(String message);
    }

    public void updateChatUI(String receivedMessage) {
       // chatArea.append(receivedMessage + "\n");  
       JPanel receiverBubble = createBubblePanel(receivedMessage, false);
    chatBubblePanel.add(receiverBubble);
    chatBubblePanel.add(Box.createVerticalStrut(5)); // Add spacing between bubbles
    scrollPane.getViewport().setView(chatBubblePanel); // Update the viewport
    chatBubblePanel.revalidate();
    chatBubblePanel.repaint();
    }

    
}


class RoundBorder extends AbstractBorder {
    private int radius;

    RoundBorder(int radius) {
        this.radius = radius;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(c.getBackground());
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.fillRoundRect(x, y, width, height, radius, radius);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius, this.radius, this.radius, this.radius);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = this.radius;
        return insets;
    }

    public boolean isBorderOpaque() {
        return true;
    }
}