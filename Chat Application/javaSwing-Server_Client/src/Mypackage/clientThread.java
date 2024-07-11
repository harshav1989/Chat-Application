
package Mypackage;

import doryan.windowsnotificationapi.fr.Notification;
import java.awt.AWTException;
import java.awt.TrayIcon;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.Socket;
import doryan.windowsnotificationapi.fr.Notification;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;


// For every client's connection we call this class
public class clientThread extends Thread{
     private String clientName = null;
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;  
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;
    

    try {
      /*
       * Create input and output streams for this client.
       */
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String name;
      while (true) {
        os.println("Enter your name.");
        name = is.readLine().trim();
        if (name.indexOf('@') == -1) {
          break;
        } else {
          os.println("The name should not contain '@' character.");
        }
      }
       try{
            Notification.sendNotification("new member joined  ", name, TrayIcon.MessageType.NONE);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(AWTException e){
            e.printStackTrace();
        }

      /* Welcome the new the client. */
      os.println("Welcome " + name
          + " to our chat room.\nTo leave enter quit in a new line.");
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] == this) {
            clientName = "@" + name;
            break;
          }
        }
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this) {
            threads[i].os.println("*** A new user " + name
                + " entered the chat room !!! ***");
         /*  */
          }
        }
      }
      /* Start the conversation. */
          while (true) {
            String line = reader.readLine();
            if ( line != null && line.trim().equalsIgnoreCase("quit")) {
              break;
            }
        /* If the message is private sent it to the given client. */
        if (line.startsWith("@")) {
          String[] words = line.split("\\s", 2);
          if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();
            if (!words[1].isEmpty()) {
              synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] != null && threads[i] != this
                      && threads[i].clientName != null
                      && threads[i].clientName.equals(words[0])) {
                    threads[i].os.println("<" + name + "> " + words[1]);
                    /*
                     * Echo this message to let the client know the private
                     * message was sent.
                     */
                    this.os.println(">" + name + "> " + words[1]);
                    break;
                  }
                }
              }
            }
          }
        } else {
          /* The message is public, broadcast it to all other clients. */
          synchronized (this) {
            for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] != null && threads[i].clientName != null) {
                threads[i].os.println("<" + name + "> " + line);
              }
            }
          }
        }
      }
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this
              && threads[i].clientName != null) {
            threads[i].os.println("*** The user " + name
                + " is leaving the chat room !!! ***");
          }
        }
      }
      os.println("*** Bye " + name + " ***");

      
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == this) {
            threads[i] = null;
          }
        }
      }
     
      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }
  
  private Map<String, clientThread> userThreadMap = new HashMap<>();
    private final Object lock = new Object(); // Lock for synchronization

    public void createUserThread(String username) {
        synchronized (lock) {
            // Create a thread for the user and associate it with their username
            clientThread newUserThread = new clientThread(clientSocket, threads);
            userThreadMap.put("@" + username, newUserThread);
            newUserThread.start();
        }
    }

 
 public void sendMessageToRecipient(String message, String recipient) {
    DataInputStream is = null;
    PrintStream os = null;

    try {
        is = new DataInputStream(clientSocket.getInputStream());
        os = new PrintStream(clientSocket.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        clientThread recipientThread = userThreadMap.get(recipient);

        if (recipientThread != null) {
            synchronized (this) {
                recipientThread.os.println("<" + recipient + "> " + message);
            }
        } else {
            System.out.println("Recipient not found.");
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


  
  
}
