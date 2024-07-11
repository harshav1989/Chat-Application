package Mypackage;

import javax.swing.JOptionPane;

public class MainApplication {
    private static DatabaseHandler databaseHandler;
  
 public static void main(String[] args) {
        databaseHandler = new DatabaseHandler();
        ChatServer chatServer = new ChatServer();
        Thread serverThread = new Thread(chatServer::startServer);
        serverThread.start();
        int n =0;
        Object[] selectioValues = { "default","group"};
		String initialSection = "default";
		
		Object selection = JOptionPane.showInputDialog(null, "Login mode : ", "MyChatApp", JOptionPane.QUESTION_MESSAGE, null, selectioValues, initialSection);
		if(selection.equals("default")){
                   n=2;
                   }
                else if(selection.equals("group")){
			String number  = JOptionPane.showInputDialog("Enter number of people ");
                        n = Integer.valueOf(number);
                }
                for(int i=0 ; i<n ; i++){
                             WelcomePage welcomePage = new WelcomePage(databaseHandler , chatServer);
                             welcomePage.setVisible(true);
        
                }  
    }
}
