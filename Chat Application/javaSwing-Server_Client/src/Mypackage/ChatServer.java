package Mypackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import javax.swing.JOptionPane;

public class ChatServer {
    private Map<String, List<String>> groupChats = new HashMap<>();
    private List<MessageObserver> observers = new ArrayList<>();
    private DatabaseHandler databaseHandler;
     private final int MAX_CLIENTS = 5; // Maximum allowed clients
    private final Semaphore semaphore = new Semaphore(MAX_CLIENTS);
    private final int serverPort = 5555;
    public Map<String, ClientHandler> clients = new HashMap<>();
  
    public ChatServer() {
        this.databaseHandler = new DatabaseHandler();
      
    }

      public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Server started on port " + serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                try {
                    semaphore.acquire(); // Acquire a permit
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                    // Handle client in a separate thread
                    Thread clientThread = new Thread(() -> handleClient(clientSocket));
                    clientThread.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Assuming continuous interaction until an "exit" command
            String input;
            while (true) {
                input = in.readLine();
                if (input.trim().equalsIgnoreCase("exit")) {
                    in.close();
                out.close();
               clientSocket.close();
               semaphore.release(); // Release the permit
                System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
                    break; // Exit the chat if the "exit" command is received
                }
                else{
                System.out.println("Received: " + input);
                // Perform appropriate error handling or reinitialization
            }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void adduser(ClientHandler client , String user){
        clients.put(user , client);
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
        String name = entry.getKey();
    ClientHandler clientHandler = entry.getValue();
    
 //   System.out.println("Username: " + name + ", ClientHandler: " + clientHandler);
}
    }
    
    public void removeuser(String username) {
    clients.remove(username);
}

    
   public void broadcastMessage(String message, String sender) {
    for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
        if (entry.getKey().equals(sender)) {
            entry.getValue().sendMessage(entry.getKey(),message, sender);
        }
    }
}

    interface MessageObserver {
    void update(String message);
}
    
    public void addObserver(MessageObserver observer) {
        observers.add(observer);
    }

    // Method to notify observers about incoming messages
    public void notifyObservers(String message) {
        for (MessageObserver observer : observers) {
            observer.update(message);
        }
    }

    public boolean isUsernameTaken(String username) {
    for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
        if (entry.getKey().equals(username)) {
                return true;
            }
        }
        return false;
    }
 public void sendPrivateMessage(String sender, String receiver, String message) {
        // Send the message to the receiver if online
        sendMessageToUser(sender, receiver, message);
        
        // Save as offline message if receiver is offline
        if (!isReceiverOnline(receiver)) {
            saveOfflineMessage(sender, receiver, message);
        }
    }
 public ClientHandler findClientHandler(String username) {
     
     for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
    String name = entry.getKey();
    ClientHandler clientHandler = entry.getValue();  
    System.out.println("Username: " + name + ", ClientHandler: " + clientHandler);
}
   for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
        if (entry.getKey().equals(username)) {
                 return entry.getValue();
        }
    }
    return null;
}
 
   void sendMessageToUser(String sender, String receiver, String message) {
        // Find the receiver's client handler
        ClientHandler receiverHandler = findClientHandler(receiver);
       // System.out.print(receiverHandler);

        if (receiverHandler != null && receiverHandler.isConnected(receiver)) {
            receiverHandler.receiveMessage(sender, receiver,message); // Modified to relay the message
        } else {
            // Handle if receiver not found or offline
            System.out.println("Receiver '" + receiver + "' not found or offline.");
            saveOfflineMessage(sender, receiver, message);
        }
    }


     void sendMessage(String sender, ClientHandler receiverH, String receiver,String message) {
        // Find the receiver's client handler
         System.out.print(receiverH);
        if (receiver != null ) {
            receiverH.receiveMessage(sender, receiver ,  message); // Modified to relay the message
        } else {
            // Handle if receiver not found or offline
            System.out.println("Receiver '" + receiver + "' not found or offline.");
            saveOfflineMessage(sender, receiver, message);
        }
    }

    boolean isReceiverOnline(String username) {
        ClientHandler receiverHandler = findClientHandler(username);
        return receiverHandler != null && receiverHandler.isConnected(username);
    }
    
     private void saveOfflineMessage(String sender, String receiver, String message) {
        // Save message as offline in the database
        boolean messageSaved = databaseHandler.saveChatHistory(sender, receiver, message, null);
        if (messageSaved) {
            System.out.println("Message saved as an offline message for " + receiver);
        } else {
            System.out.println("Failed to save offline message for " + receiver);
        }
    }

    // Method to handle client disconnection
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcastMessage(client.getUsername() + " has disconnected.", null);
         client.closeClient();
    }

public void sendGroupMessage(String sender, List<String> receivers, String message) {
    // Send the group message to all receivers
    for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
          if (receivers.contains(entry.getKey())) {
              System.out.print(entry.getKey() );
            entry.getValue().sendMessage(sender + ": " + message);
            ClientHandler receiverHandler = findClientHandler(entry.getKey());
       // System.out.print(receiverHandler);

        if (receiverHandler != null && receiverHandler.isConnected(entry.getKey())) {
            receiverHandler.receiveGMessage(sender, entry.getKey(),message); // Modified to relay the message
        } else {
            // Handle if receiver not found or offline
            System.out.println("Receiver '" + entry.getKey() + "' not found or offline.");
            saveOfflineMessage(sender, entry.getKey(), message);
        }
        }
    }
    
    // Save group chat message to database
    for (String receiver : receivers) {
        databaseHandler.saveChatHistory(sender, receiver, message, null);
    }
}
    public String generateGroupName() {
        return "Group_" + System.currentTimeMillis();
    }

 public void createGroupChat(List<String> members) {
        String groupName = generateGroupName(); // Method to generate a unique group name
        groupChats.put(groupName, members);
    }

  // Method to handle user status updates
    public void updateUserStatus(String username, boolean status) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
         if (entry.getKey().equals(username)) {
                entry.getValue().setUserStatus(status);
                break;
            }
        }
    }

    // Method to retrieve user status
    public boolean getUserStatus(String username) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
         if (entry.getKey().equals(username)) {
             return true;
            }
        }
        return false; // If user not found or offline
    }

// ServerMain.java - Entry point of the server application
    
 public void updateClientStatus(ClientHandler client, boolean status) {
    // Update client status (online/offline) in the server
    client.setOnline(status);

    // Update the client's status in the server's list of clients
    for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
         if (entry.getValue().equals(client)) {
                entry.getValue().setOnline(status);
            break;
        }
    }

    // Additional logic to handle status updates
    if (status) {
        broadcastMessage(client.getUsername() + " is now online.", null);
    } else {
        broadcastMessage(client.getUsername() + " is now offline.", null);
        // If offline, save the user's status in the database
        databaseHandler.updateUserStatus(client.getUsername(), false);
    }
}

}
