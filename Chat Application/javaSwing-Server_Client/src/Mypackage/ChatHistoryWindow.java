/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mypackage;

/**
 *
 * @author MY PC
 */
import java.util.List;
import javax.swing.*;

public class ChatHistoryWindow extends JFrame {
    private JTextArea chatHistoryArea;
    private final DatabaseHandler databaseHandler;
    private final String currentUser;
    private final String selectedUser;

    public ChatHistoryWindow(DatabaseHandler databaseHandler, String currentUser, String selectedUser) {
        this.databaseHandler = databaseHandler;
        this.currentUser = currentUser;
        this.selectedUser = selectedUser;

        setTitle("Chat History between " + currentUser + " and " + selectedUser);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        displayChatHistory();
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        chatHistoryArea = new JTextArea();
        chatHistoryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatHistoryArea);
        scrollPane.setBounds(10, 10, 360, 300);
        panel.add(scrollPane);
    }

    private void displayChatHistory() {
    List<String> chatHistoryList = databaseHandler.getChatHistory(currentUser, selectedUser);

    StringBuilder chatHistoryBuilder = new StringBuilder();
    for (String message : chatHistoryList) {
        chatHistoryBuilder.append(message).append("\n");
    }

    String chatHistory = chatHistoryBuilder.toString();
    chatHistoryArea.setText(chatHistory);
}

}
