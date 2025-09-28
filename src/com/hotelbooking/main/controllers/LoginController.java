package com.hotelbooking.main.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.*;

import com.hotelbooking.main.db.DatabaseHelper;
import com.hotelbooking.main.ui.ForgotPassword;
import com.hotelbooking.main.ui.LoginUI;
import com.hotelbooking.main.ui.RegisterUI;
import com.hotelbooking.main.ui.DashboardUI;


public class LoginController implements ActionListener, KeyListener {
    private final LoginUI view;
    private final DatabaseHelper db;
    public LoginController (LoginUI view) {
        this.view = view;
        this.db = new DatabaseHelper();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonName = e.getActionCommand();
        if (Objects.equals(buttonName, "register_user")) {
            new RegisterUI().setVisible(true);
            view.dispose();
        } else if (Objects.equals(buttonName,"login_user")) {
            handleLogin();
        } else if (Objects.equals(buttonName,"forgot_password")) {
            new ForgotPassword().setVisible(true);
            view.dispose();
         }
    }

    public void handleLogin() {

        String username = view.getUsername();
        char[] passwordChars = view.getPassword();
        // Convert char[] to String for validation (do this as late as possible)
        String password = new String(passwordChars);

        Arrays.fill(passwordChars, '0'); // clear sensitive data
//        if (Objects.equals(username, "admin") && password.equals("1234")) {
//            new DashboardUI().setVisible(true);
//            view.dispose();
//        }
//        else{
//            JOptionPane.showMessageDialog(new JPanel(),"Invalid username or password! Try Again","Incorrect Credentials",JOptionPane.ERROR_MESSAGE);
//        }

        if (db.validateLogin(username, password)) {
            new DashboardUI().setVisible(true);
            view.dispose();
        }
        else{
            JOptionPane.showMessageDialog(view,"Invalid username or password! Try Again","Login Failed",JOptionPane.ERROR_MESSAGE);
        }
        password = null;

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
