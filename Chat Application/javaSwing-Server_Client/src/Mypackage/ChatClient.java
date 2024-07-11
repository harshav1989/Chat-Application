package Mypackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import doryan.windowsnotificationapi.fr.Notification;
import java.awt.TrayIcon.MessageType;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;



// Class to manage Client chat Box.
public class ChatClient {

    static ChatAccess ChatAccess;
    String line;
     private static boolean isOnline = true;
    private static String statusMessage = "Online";
    private ChatAccess chatAccess; 
    private Map<String, ChatAccess> userChatAccessMap;
    private String currentUser;
    
    
    public ChatClient() {
        chatAccess = new ChatAccess();
        userChatAccessMap = new HashMap<>(); 
    }
    
    public ChatAccess getChatAccess() {
    return chatAccess;
}
    
    public void sendMessageToUser(String message, String recipientUsername) {
        String formattedMessage = "@" + recipientUsername + " " + message;
        chatAccess.send(formattedMessage);
    }
    
    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String user) {
        currentUser = user;
    }

    public ChatAccess getChatAccessForUser(String username) {
        // Retrieve the ChatAccess for the given username
        return userChatAccessMap.get(username);
    }

    public void setUserChatAccess(String username, ChatAccess chatAccess) {
        userChatAccessMap.put(username, chatAccess);
    }

    public void closeConnection() {
        if (chatAccess != null) {
            chatAccess.close();
            // Additional cleanup or handling if required
            System.out.println("Connection closed successfully.");
        } else {
            System.out.println("No active connection to close.");
        }
    }

    /** Chat client access */
    class ChatAccess extends Observable {
        private Socket socket;
        private OutputStream outputStream;

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }
        
        public ChatAccess getChatAccess() {
        return chatAccess;
        }

        /** Create socket, and receiving thread */
        public void InitSocket(String server, int port) throws IOException {
            socket = new Socket(server, port);
            outputStream = socket.getOutputStream();

            Thread receivingThread;
            receivingThread = new Thread() {
                
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null)
                            notifyObservers(line);
                      
                    } catch (IOException ex) {
                        notifyObservers(ex);
                    }
                }
            };
            receivingThread.start();
        }

        private static final String CRLF = "\r\n"; // newline

        /** Send a line of text */
        public void send(String text) {
            try {
                outputStream.write((text + CRLF).getBytes());
                outputStream.flush();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
            try{
            Notification.sendNotification("Chat GUI ", text, MessageType.NONE);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(AWTException e){
            e.printStackTrace();
        }
        }

        /** Close the socket */
        public void close() {
            try {
                socket.close();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }
    
    public void associateChatAccessForUser(String selectedUser, ChatAccess selectedChatAccess) {
        setUserChatAccess(selectedUser, selectedChatAccess);
    }
    
    // Adding a method to associate chat access for the current user
    public void associateChatAccessForCurrentUser(ChatAccess currentUserChatAccess) {
        setUserChatAccess(currentUser, currentUserChatAccess);
    }


    /** Chat client UI */
    static class ChatFrame extends JFrame implements Observer {

        private JTextArea textArea;
        private JTextField inputTextField;
        private JButton sendButton;
        private ChatAccess chatAccess;
        private JLabel statusLabel;
        private ChatAccess chattAccess;

        public ChatFrame(ChatAccess chatAccess) {
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            buildGUI();
        }

        /** Builds the user interface */
        private void buildGUI() {
            textArea = new JTextArea(20, 50);
             statusLabel = new JLabel("Status: " + statusMessage);
            add(statusLabel, BorderLayout.NORTH);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            add(new JScrollPane(textArea), BorderLayout.CENTER);

            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            inputTextField = new JTextField();
            sendButton = new JButton("Send");
            box.add(inputTextField);
            box.add(sendButton);

            // Action for the inputTextField and the goButton
            ActionListener sendListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String str = inputTextField.getText();
                    if (str != null && str.trim().length() > 0){
                         if (str.trim().equalsIgnoreCase("quit")) {
                // Change status and close the window
                chatAccess.close();
                isOnline = false;
                statusMessage = "Offline";
                dispose(); // Close the window
            } else {  
                        chatAccess.send(str);
                         }
                    }
                    inputTextField.selectAll();
                    inputTextField.requestFocus();
                    inputTextField.setText("");
              }
            };
            inputTextField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    chatAccess.close();
                        isOnline = false;
                    statusMessage = "Offline";
                }
            });
        }

        /** Updates the UI depending on the Object argument */
        public void update(Observable o, Object arg) {
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(finalArg.toString());
                    textArea.append("\n");
                }
            });
        }
    }
    


    public static void main(String[] args) {
        String server = args[0];
        int port =2222;
        //ChatAccess access = new ChatAccess();
        
        ChatClient client = new ChatClient();
        ChatAccess access = client.new ChatAccess(); // Creating an instance of ChatAccess
        
         client.setUserChatAccess("harsh12", access);

        JFrame frame = new ChatFrame(access);
        frame.setTitle("MyChatApp - connected to " + server + ":" + port + " - " + statusMessage);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        try {
            access.InitSocket(server,port);
        } catch (IOException ex) {
            System.out.println("Cannot connect to " + server + ":" + port);
            ex.printStackTrace();
            System.exit(0);
        }
            access = client.getChatAccess();
    }
    
     void send(String message) {
     chatAccess.send(message); 
     }

}