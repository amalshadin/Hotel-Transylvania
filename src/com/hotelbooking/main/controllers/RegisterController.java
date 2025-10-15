package com.hotelbooking.main.controllers;

import com.hotelbooking.main.db.DatabaseHelper;
import com.hotelbooking.main.ui.LoginUI;
import com.hotelbooking.main.ui.RegisterUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class RegisterController implements ActionListener {
    private final RegisterUI view;
    private final DatabaseHelper db;
    public RegisterController(RegisterUI view) {
        this.view = view;
        this.db = new DatabaseHelper();
    }
    public void actionPerformed (ActionEvent e) {
        String buttonName = e.getActionCommand();
        if (Objects.equals(buttonName,"go_back_to_login_page")) {
            new LoginUI().setVisible(true);
            view.dispose();
        }else if (Objects.equals(buttonName,"registration_successful")) {
            String username = view.getUsername();
            String password = new String(view.getPassword());
            String name = view.getFullName();
            String mobile = view.getMobileNo();
            String[] optionSelect = {"OK","NO"};
            if (db.registerUser(username, password, name, mobile)) {
                int optionSelector = JOptionPane.showOptionDialog(
                        null,
                        "Registration Successful! Continue To Login?",
                        "Registration Completed",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        optionSelect,
                        optionSelect[0]
                );
                if (optionSelector == 0) {
                    new LoginUI().setVisible(true);
                    view.dispose();
                } else if (optionSelector == 1) {
                    new RegisterUI().setVisible(true);
                    view.dispose();
                }
            }else {
                JOptionPane.showMessageDialog(view,
                        "Registration failed. Username may already exist or" + '\n' +
                                " registration details hasn't been completely filled.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
