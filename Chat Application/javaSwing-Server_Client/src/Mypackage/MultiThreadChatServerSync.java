

package Mypackage;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
/**
 *
 * @author mohammed
 */

// the Server class
public class MultiThreadChatServerSync {
   // The server socket.
  private static ServerSocket serverSocket = null;
  // The client socket.
  private static Socket clientSocket = null;

  // This chat server can accept up to maxClientsCount clients' connections.
  private static final int maxClientsCount = 10;
  private static final clientThread[] threads = new clientThread[maxClientsCount];
  
  private static boolean authenticateUser(String username, String password) {
    // Simulating user authentication (replace with more secure logic)
    return username.equals("user") && password.equals("password");
}
 
  public static void main(String args[]) {

    // The default port number.
    int portNumber = 2222;
    if (args.length < 1) {
      System.out.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
          + "Now using port number=" + portNumber);
    } else {
      portNumber = Integer.valueOf(args[0]).intValue();
    }

    
  //   * Open a server socket on the portNumber (default 2222). Note that we can
   //  * not choose a port less than 1023 if we are not privileged users (root).
     
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    
  //   * Create a client socket for each connection and pass it to a new client
    // * thread.
     
    while (true) {
      try {
        clientSocket = serverSocket.accept();
        int i = 0;
        String username = "user"; // Replace with retrieved username
            String password = "password"; // Replace with retrieved password

            if (authenticateUser(username, password)) {
        for (i = 0; i < maxClientsCount; i++) {
          if (threads[i] == null) {
            (threads[i] = new clientThread(clientSocket, threads)).start();
            break;
          }
        }
        if (i == maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server too busy. Try later.");
          os.close();
          clientSocket.close();
        }
      } else {
                // Authentication failed
                PrintStream os = new PrintStream(clientSocket.getOutputStream());
                os.println("Authentication failed. Please check your credentials.");
                os.close();
                clientSocket.close();
            }
        } 
            catch (IOException e) {
        System.out.println(e);
      }
    }
  }  

    void closeServer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
