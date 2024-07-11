
package Mypackage;

import doryan.windowsnotificationapi.fr.Notification;
import java.awt.AWTException;
import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;
import javax.swing.ImageIcon;

public class ClientHandler implements Runnable{
    private final String serverAddress = "127.0.0.1";
    private final int serverPort = 5555;
    private final String username;
    private final BufferedReader consoleInput;
    private boolean isConnected;
    private  Socket clientSocket;
    private final ChatServer chatServer;
    private PrintWriter out;
    private BufferedReader in;
    private DatabaseHandler databaseHandler;
    private String seluser;
    private ChatPage chatPage;
  // private Map<String, ClientHandler> clients = new HashMap<>();
   public Map<String, ChatPage> chatPages = new HashMap<>();
   public Map<String, GroupChatPage> GPages = new HashMap<>();


   public ClientHandler( int serverPort, String username, ChatServer chatServer) {
      
        this.username = username;
        this.consoleInput = new BufferedReader(new InputStreamReader(System.in));
        this.isConnected = false;
        this.chatServer = chatServer; // Initialize the chatServer here
        
    }
      
 /*   ClientHandler(Socket clientSocket, ChatServer aThis, DatabaseHandler databaseHandler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/

// When a client disconnects, remove its entry from the clients map
   /*
public void removeClient(String username) {
    clients.remove(username);
}
    public void addclient(String username , ClientHandler clientH){
        clients.put(username, clientH);
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
        String name = entry.getKey();
    ClientHandler clientHandler = entry.getValue();
    
    //System.out.println("Username: " + name + ", ClientHandler: " + clientHandler);
}
    }

public ClientHandler findClientHandler(String user) {
    
    for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
        if (entry.getKey().equals(user)) {
            return entry.getValue();
            
        }
        }
    return null;

}     // Return null if the username is not found
    */
    public GroupChatPage findGroupChatPage(String user) {
    
     for (Map.Entry<String, GroupChatPage> entry : GPages.entrySet()) {
            if (entry.getKey().equals(user)) {
                return entry.getValue();        
            }
            }
        return null;
    }    



   
   public List<String> getChatHistory(String selectedUser) {
        // Use DatabaseHandler to fetch chat history
        DatabaseHandler databaseHandler = new DatabaseHandler();
        return databaseHandler.getChatHistory(username, selectedUser);
    }
   public void initiateChatWithUser(String selectedUser, DatabaseHandler dbHandler, String loggedInUser, ChatServer server, ChatPage chatPage) {
     // this.seluser = selectedUser;
      //  this.chatPage = chatPage; // Assign the ChatPage reference
     //   chatPage = new ChatPage(selectedUser, this, dbHandler, loggedInUser, server);
       // chatPage.setVisible(true);
    }

public void sendMessage(String receiver, String message, String sender) {
    ClientHandler receiverHandler = chatServer.findClientHandler(receiver);

        try {
            // Send an indication of receiver being offline to the sender
            if (!chatServer.isReceiverOnline(receiver)) {
                sendMessageToUser(sender, "the user is currently offline.");
            } else {
                // Assuming you have a reference to the GroupChatPage instance of the receiver
                GroupChatPage receiverGroupChatPage = findGroupChatPage(receiver); // Replace with the actual method to find the GroupChatPage
                System.out.print( receiverGroupChatPage);
                if (receiverGroupChatPage != null) {
                    receiverGroupChatPage.updateChatUI( message);
                } else {
                    System.out.println("Receiver's GroupChatPage instance not found.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions related to sending the offline message to the sender
        }
        if (receiverHandler != null && receiverHandler.isConnected(receiver)) {
        receiverHandler.receiveMessage(sender, receiver, message); // Relay message to receiver's handler
        } else {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        boolean messageSaved = databaseHandler.saveChatHistory(sender, receiver, message, "");


        
        if (messageSaved) {
            System.out.println("Message saved as an offline message for " + receiver);
        } else {
            System.out.println("Failed to save offline message for " + receiver);
        }
    }
}

public void receiveMessage(String sender , String receiver ,String message ) {
     for (Map.Entry<String, ChatPage> entry : chatPages.entrySet()) {
        String username = entry.getKey();
        ChatPage chatPage = entry.getValue();
        System.out.println("Username: " + username + ", ChatPage: " + chatPage);
     }
   ChatPage receiverChatPage = chatPages.get(receiver);
    if (receiverChatPage != null) {
        receiverChatPage.updateChatUI(sender + ": " + message);
         try{
            Notification.sendNotification(sender, message, TrayIcon.MessageType.NONE);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(AWTException e){
            e.printStackTrace();
        }
    }
    // Notify the sender about the received message if they're offline
    if (!isConnected(receiver)) {
        sendMessageToUser(sender, "The user is currently offline.");
    }
}

public void receiveGMessage(String sender , String receiver ,String message ) {
     for (Map.Entry<String, GroupChatPage> entry : GPages.entrySet()) {
        String username = entry.getKey();
        GroupChatPage groupchatPage = entry.getValue();
        System.out.println("Username: " + username + ", ChatPage: " + chatPage);
     }
   GroupChatPage receiverChatPage = GPages.get(receiver);
    if (receiverChatPage != null) {
        receiverChatPage.updateChatUI(sender + ": " + message);
         try{
            Notification.sendNotification(sender + "sent", message, TrayIcon.MessageType.NONE);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(AWTException e){
            e.printStackTrace();
        }
    }
    // Notify the sender about the received message if they're offline
    if (!isConnected(receiver)) {
        sendMessageToUser(sender, "The user is currently offline.");
    }
}


    public void closeChat(String selectedUser) {
        // Logic to close the chat with a selected user
        // For now, no specific implementation as provided in the current code
        System.out.println("Chat with " + selectedUser + " closed.");
        
    }

    public void run() {
    try {
        // Connect to the server
        Socket socket = new Socket(serverAddress, serverPort);
        this.clientSocket = socket;

        // Set up communication streams
         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         out = new PrintWriter(socket.getOutputStream(), true);

        // Send the username to the server for identification
        out.println(username);

        // Create a separate thread for receiving messages from the server
        Thread receiverThread = new Thread(() -> {
                try {
                    while (true) {
                        String receivedMessage = in.readLine();
                         if (receivedMessage != null) {
                            // Updated to handle received messages
                            receiveMessage( username , seluser, receivedMessage );
                        } else {
                            System.out.println("Disconnected from the server.");
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isConnected = false;
            });
            receiverThread.start();
            isConnected = true;
           
            // Handle user input, sending messages, etc.
            String userInput;
             while ((userInput = in.readLine()) != null) {
           // out.println(userInput);
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Disconnecting from the server...");
                break;
            }
        }
             closeClient();
    } catch (IOException e) {
        System.err.println("IOException in client: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {
            // Close resources in the finally block
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
    }
    
    public void closeClient() {
    try {
        if (out != null) out.close();
        if (in != null) in.close();
        if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
    } catch (IOException e) {
        // Handle closing errors
    }
}
 
 boolean isConnected(String user) {
    if( chatServer.clients.containsKey(user)){
        return true;
    }
    return false;
}
    //check this line again 
 /*    boolean isGConnected(String user) {
         return clients.containsKey(user);
    }
  */
  public void sendMessageToUser(String receiver, String message) {
    chatServer.sendPrivateMessage(username, receiver, message);
}

    void sendMessage(String message) {
    try {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(message);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public String  getUsername() {
    return username;
}

void setOnline(boolean status) {
     isConnected = status;
}

   void setUserStatus(boolean status) {
    // Assuming this method updates the online status of the client
    chatServer.updateClientStatus(this, status);
}
   boolean getUserStatus() {
    return isConnected;
}
   
       
    public void sendImage( String sender, String receiver , String imagePath) {
        ClientHandler receiverHandler = chatServer.findClientHandler(receiver);
         System.out.println("Receiver   " + receiver + "  " + receiverHandler +"   Sender  " + sender);
        //&& receiverHandler.isConnected(receiver)
        if (receiverHandler != null ) {
               receiverHandler.receiveImage(receiver,imagePath); // Modified to relay the message
            } else {
               // Handle if receiver not found or offline
               System.out.println("Receiver '" + receiver + "' not found or offline.");
              // saveOfflineMessage(sender, receiver, ,imagePath);
          }
    }       


     void receiveImage( String receiver, String imagePath) {
        //ChatPage receiverChatPage =null;
        try {
            // Load the image file from the given path
            File imageFile = new File(imagePath);
            for (Map.Entry<String, ChatPage> entry : chatPages.entrySet()) {
                receiver = entry.getKey();
               //receiverChatPage = entry.getValue();
                System.out.println("Username: " + username + ", ChatPage: " + chatPage);
                 }
             ChatPage receiverChatPage = chatPages.get(receiver);
            if (receiverChatPage != null) {
                ImageIcon imageIcon = new ImageIcon(imagePath);
                receiverChatPage.displayReceivedImage(imageIcon);
                System.out.print("image sent \n ");
            } else {
                System.out.println("Receiver's ChatPage instance not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions related to receiving and displaying the image
        }
    }
   
}

 class FileReceiver {
  /*  public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5555)) {
            System.out.println("FileReceiver is waiting for a connection...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection established with " + clientSocket.getInetAddress());

            receiveFile(clientSocket);

            System.out.println("File received successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    private static void receiveFile(Socket socket) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            String fileType = dis.readUTF();
            if ("FILE".equals(fileType)) {
                String fileName = dis.readUTF();
                System.out.println("Receiving file: " + fileName);

                try (FileOutputStream fos = new FileOutputStream(fileName)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = dis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }

                System.out.println("File received and saved: " + fileName);
            } else {
                System.out.println("Invalid file type received.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 }