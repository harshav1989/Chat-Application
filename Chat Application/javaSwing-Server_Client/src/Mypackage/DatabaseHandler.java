
package Mypackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/chat_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin";

    private Connection connection;

    public DatabaseHandler() {
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyLogin(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Return true if there's a match for username and password
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(String name, String username, String password) {
        try {
            String query = "INSERT INTO users (name, username, password) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, username);
            statement.setString(3, password);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0; // Return true if the user was successfully registered
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        try {
            String query = "SELECT username FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean saveChatHistory(String user1, String user2, String message, String media) {
        try {
            String query = "INSERT INTO chat (user1, user2, message, media) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user1);
            statement.setString(2, user2);
            statement.setString(3, message);
            statement.setString(4, media);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0; // Return true if the chat was successfully saved
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getChatHistory(String user1, String user2) {
        List<String> chatHistory = new ArrayList<>();
        try {
            String query = "SELECT message FROM chat WHERE (user1 = ? AND user2 = ?) OR (user1 = ? AND user2 = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user1);
            statement.setString(2, user2);
            statement.setString(3, user2);
            statement.setString(4, user1);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                chatHistory.add(resultSet.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatHistory;
    }

    // Other database-related methods
public List<String> getUsers() {
    List<String> users = new ArrayList<>();
    ResultSet resultSet = null;
    PreparedStatement statement = null;

    try {
        String query = "SELECT username FROM users";
        statement = connection.prepareStatement(query);
        resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String username = resultSet.getString("username");
            users.add(username);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle any exceptions here
    } finally {
        // Close resources in reverse order of their creation
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    return users;
}

    
    public boolean isUserExists(String username) {
    boolean userExists = false;
       PreparedStatement statement = null;
    ResultSet resultSet = null;

    try {
        // Establish a database connection
                // Prepare the SQL statement to check for the existence of the username
        String query = "SELECT COUNT(*) AS count FROM users WHERE username = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, username);

        // Execute the query
        resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int count = resultSet.getInt("count");
            userExists = (count > 0); // If count > 0, user exists
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Handle the exception according to your application's error handling strategy
    } finally {
        // Close resources in a finally block to ensure they're closed regardless of exceptions
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            
        } catch (SQLException e) {
            e.printStackTrace(); // Handle closing exceptions if needed
        }
    }

    return userExists;
}
    
    public void updateUserStatus(String username, boolean status) {
    try {
        String query = "UPDATE users SET online_status = ? WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setBoolean(1, status);
        statement.setString(2, username);
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("User status updated successfully for " + username);
        } else {
            System.out.println("Failed to update user status for " + username);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
