package Mypackage;

import java.util.List;

public class MessageHistory {

    private final DatabaseHandler databaseHandler;

    public MessageHistory(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

   

    public void saveHistory(String sender, String receiver, String message) {
        // Use the method from DatabaseHandler to save chat history
        databaseHandler.saveChatHistory(sender, receiver, message, null);
    }

    public List<String> loadHistory(String currentUser, String selectedUser) {
        // Use the method from DatabaseHandler to get chat history
        return databaseHandler.getChatHistory(currentUser, selectedUser);
    }
    



}
