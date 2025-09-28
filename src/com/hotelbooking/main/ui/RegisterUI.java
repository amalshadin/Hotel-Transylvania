package com.hotelbooking.main.ui;

import com.hotelbooking.main.controllers.RegisterController;
import com.hotelbooking.main.uicomponents.ImagePanel;
import com.hotelbooking.main.uicomponents.Placeholder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RegisterUI extends JFrame {

    private final JTextField fullName, email,mobileNo;
    private final JPasswordField passwordField,confirmPasswordField;
    private Placeholder fullNamePlaceholder,emailPlaceholder,mobileNoPlaceholder,passwordPlaceholder,confirmPasswordPlaceholder;

    public RegisterUI() {
        this.setSize(new Dimension(1024,576));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setTitle("Register");

        this.setLayout(new MigLayout("fill,insets 0","[60%]0.5[40%]","grow"));

        ImagePanel registerImagePanel = new ImagePanel("/registerImage.jpeg");
        JPanel registrationPanel = new JPanel();

        registrationPanel.setBackground(new Color(0xFFFFFF));

        JLabel newUserLabel = new JLabel("Register");
        newUserLabel.setFont(new Font("Basic",Font.BOLD,24));
        newUserLabel.setBackground(null);
        newUserLabel.setOpaque(true);
        newUserLabel.setForeground(Color.BLACK);

        registrationPanel.setLayout(new MigLayout(
                "fillx,insets 25,wrap 1",
                "[]",
                "10%[]15[]10[]10[]10[]10[]10[][]10%"
        ));

        fullName = new JTextField();
        email = new JTextField();
        mobileNo = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        JButton registerButton = new JButton("Register");
        JButton goToLoginButton = new JButton("Login");



        fullName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fullName.setBackground(new Color(0xEDEDED));
        fullName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDBDBDB), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8) // Add internal padding
        ));

        email.setBackground(new Color(0xEDEDED));
        email.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDBDBDB), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8) // Add internal padding
        ));

        mobileNo.setBackground(new Color(0xEDEDED));
        mobileNo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDBDBDB), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8) // Add internal padding
        ));

        passwordField.setBackground(new Color(0xEDEDED));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDBDBDB), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8) // Add internal padding
        ));

        confirmPasswordField.setBackground(new Color(0xEDEDED));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDBDBDB), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8) // Add internal padding
        ));


        registerButton.setBackground(new Color(0x007BFF));
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        registerButton.setForeground(Color.white);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
        registerButton.setActionCommand("registration_successful");
        registerButton.addActionListener(new RegisterController(this));

        JPanel goToLoginPanel = new JPanel(new FlowLayout());
        goToLoginPanel.setBackground(null);
        JLabel goToLoginLabel = new JLabel("Already have an account?");
        goToLoginLabel.setBackground(null);

        goToLoginButton.setContentAreaFilled(false);
        goToLoginButton.setBorder(null);
        goToLoginButton.setForeground(new Color(0x173DFF));
        goToLoginButton.setFocusPainted(false);
        goToLoginButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        goToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goToLoginButton.setActionCommand("go_back_to_login_page");
        goToLoginButton.addActionListener(new RegisterController(this));

        goToLoginPanel.add(goToLoginLabel);
        goToLoginPanel.add(goToLoginButton);

        //Adding PlaceHolders
        fullNamePlaceholder = new Placeholder(fullName,"Full Name");
        fullNamePlaceholder.addPlaceholder();

        emailPlaceholder = new Placeholder(email,"E-mail");
        emailPlaceholder.addPlaceholder();

        mobileNoPlaceholder = new Placeholder(mobileNo,"Mobile");
        mobileNoPlaceholder.addPlaceholder();

        passwordPlaceholder = new Placeholder(passwordField,"Password");
        passwordPlaceholder.addPlaceholder();

        confirmPasswordPlaceholder = new Placeholder(confirmPasswordField,"Confirm Password");
        confirmPasswordPlaceholder.addPlaceholder();

        //Adding Registration components to the RegistrationPanel
        registrationPanel.add(newUserLabel,"align left");
        registrationPanel.add(fullName,"growx, h 45!");
        registrationPanel.add(email,"growx, h 45!");
        registrationPanel.add(mobileNo,"growx, h 45!");
        registrationPanel.add(passwordField,"growx, h 45!");
        registrationPanel.add(confirmPasswordField,"growx, h 45!");
        registrationPanel.add(registerButton,"growx,h 45!");
        registrationPanel.add(goToLoginPanel,"growx");


        this.add(registerImagePanel,"cell 0 0,grow");
        this.add(registrationPanel,"cell 1 0,grow");
        registerImagePanel.setVisible(true);
        registrationPanel.setVisible(true);

    }
    public String getFullName () {return fullName.getText().trim();}
    public String getMobileNo () {return mobileNo.getText().trim();}
    public String getUsername () {return email.getText().trim();}
    public char[] getPassword () {return passwordField.getPassword();}
    public char[] getConfirmPassword () {return confirmPasswordField.getPassword();}

}
