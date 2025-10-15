package com.hotelbooking.main.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import com.hotelbooking.main.controllers.LoginController;
import com.hotelbooking.main.uicomponents.ImagePanel;

import net.miginfocom.swing.MigLayout;

public class LoginUI extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginUI() {
        // --- Frame Setup (No changes here) ---
        this.setSize(new Dimension(1024, 576));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setTitle("Hotel Transylvania");
        this.setLayout(new MigLayout("fill, insets 5", "[40%][60%]", "grow"));

        // --- Left Panel for Login ---
        JPanel loginPanel = new JPanel(new MigLayout(
                "fillx, insets 20, wrap 1", // Layout constraints: fill horizontally, add padding, one component per row
                "[grow]",                  // Column constraints: one column that grows
                "25%[]15[]10[]10[]"        // Row constraints: 25% gap at top, then auto rows with gaps
        ));
        loginPanel.setBackground(Color.WHITE);

        // --- Right Panel for Image ---
        JPanel loginImagePanel = new ImagePanel("/login.jpeg");

        // --- Login Component Declaration ---
        JLabel welcomeBackLabel = new JLabel("Welcome Back");
        welcomeBackLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeBackLabel.setForeground(Color.BLACK);

        JLabel loginSubLabel = new JLabel("Login to your account");
        loginSubLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loginSubLabel.setForeground(Color.GRAY);

        //Instantiating Username and password fields
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton forgotPasswordButton = new JButton("Forgot Password?");

        JPanel registerPanel = new JPanel(new FlowLayout());
        JLabel registerLabel = new JLabel();
        JButton register = new JButton("Register");

        // --- Add Placeholder Text functionality ---
        addPlaceholderStyle(usernameField, "Username");
        addPlaceholderStyle(passwordField, "Password");

        // --- Styling ---
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);
        Color fieldBgColor = new Color(0xEDEDED); // Solid, slightly less harsh gray

        usernameField.setFont(fieldFont);
        usernameField.setBackground(fieldBgColor);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDBDBDB), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8) // Add internal padding
        ));

        passwordField.setFont(fieldFont);
        passwordField.setBackground(fieldBgColor);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDBDBDB), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8) // Add internal padding
        ));
        passwordField.setEchoChar((char) 0); // Initially show placeholder text

        // Style the login button
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBackground(new Color(0x007BFF));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loginButton.setActionCommand("login_user");
        loginButton.addActionListener(new LoginController(this));


        // Style the "Forgot Password" button to look like a link
        forgotPasswordButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        forgotPasswordButton.setForeground(new Color(0x173DFF));
        forgotPasswordButton.setFocusPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setBorder(null);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setActionCommand("forgot_password");
        forgotPasswordButton.addActionListener(new LoginController(this));

        //Register button
        registerPanel.setBackground(null);
        registerLabel.setText("Don't have an account? ");
        register.setForeground(Color.BLUE);
        register.setBorder(null);
        register.setFocusPainted(false);
        register.setContentAreaFilled(false);
        register.setCursor(new Cursor(Cursor.HAND_CURSOR));
        register.setActionCommand("register_user");
        register.addActionListener(new LoginController(this));

        this.addKeyListener(new LoginController(this));

        //Add registration components to the registration panel in the login frame
        registerPanel.add(registerLabel);
        registerPanel.add(register);
        registerPanel.add(forgotPasswordButton);

        // --- Adding components to the login panel ---
        loginPanel.add(welcomeBackLabel, "gapbottom 5"); // Add gap to the next component
        loginPanel.add(loginSubLabel, "gapbottom 20");
        loginPanel.add(usernameField, "growx, h 45!"); // Use layout constraints for size
        loginPanel.add(passwordField, "growx, h 45!");
        loginPanel.add(forgotPasswordButton, "align right, gapbottom 15"); // Align to the right
        loginPanel.add(loginButton, "growx, h 45!");
        loginPanel.add(registerPanel,"growx, h 45!");

        // --- Adding Panels to Frame ---
        this.add(loginPanel, "grow");
        this.add(loginImagePanel, "grow");
    }

    /**
     * A helper method to add placeholder text functionality to a text field.
     */
    public void addPlaceholderStyle(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        // Special handling for JPasswordField
        if (textField instanceof JPasswordField passwordField) {
            passwordField.setEchoChar((char) 0); // Show placeholder text
        }

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                    if (textField instanceof JPasswordField) {
                        ((JPasswordField) textField).setEchoChar('•'); // Set echo char on focus
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                    if (textField instanceof JPasswordField) {
                        ((JPasswordField) textField).setEchoChar((char) 0); // Hide echo char
                    }
                }
            }
        });
    }

    /**
     * getter method for username and password
     */

    public String getUsername() {
        return usernameField.getText().trim();
    }
    public char[] getPassword() {
        return passwordField.getPassword();
    }


}