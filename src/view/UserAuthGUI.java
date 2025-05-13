package view;

import models.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserAuthGUI extends JFrame {
    private UserManager userManager; // Instance of UserManager
    private JTextField usernameField; // Field for username input
    private JPasswordField passwordField; // Field for password input

    public UserAuthGUI(UserManager userManager) {
        this.userManager = userManager; // Initialize UserManager

        // Set up the frame
        setTitle("User  Authentication");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2)); // Use grid layout for form

        // Add components for username
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        // Add components for password
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        // Add login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (userManager.loginUser (username, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    new MusicPlayerGUI().setVisible(true); // Open main GUI
                    dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password."); // Notify user of failure
                }
            }
        });
        add(loginButton);

        // Add register button
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (userManager.registerUser (username, password)) {
                    JOptionPane.showMessageDialog(null, "Registration successful!");
                } else {
                    JOptionPane.showMessageDialog(null, "Username already taken.");
                }
            }
        });
        add(registerButton);
    }
}
