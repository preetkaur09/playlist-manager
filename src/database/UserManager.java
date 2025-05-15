//This class manages user registration and authentication.
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    // Method to register a new user
    public boolean registerUser (String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password); // In a real application, hash the password
            statement.executeUpdate();
            return true; // Registration successful
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false; // Registration failed
        }
    }

    // Method to check if a user exists
    public boolean userExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if a user with the given username exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to login a user
    public boolean loginUser (String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password); // In a real application, hash the password
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if login is successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Login failed
        }
    }
}
