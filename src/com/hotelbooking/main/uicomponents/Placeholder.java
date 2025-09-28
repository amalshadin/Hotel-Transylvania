package com.hotelbooking.main.uicomponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class Placeholder {
    JTextField textField;
    String placeholder;
    public Placeholder (JTextField textField,String placeholder) {
        this.textField = textField;
        this.placeholder = placeholder;
    }
    public Placeholder (JPasswordField passwordField,String placeholder) {
        this.textField = passwordField;
        this.placeholder = placeholder;
    }
    public void addPlaceholder() {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
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
                        ((JPasswordField) textField).setEchoChar('•');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                    if (textField instanceof JPasswordField) {
                        ((JPasswordField) textField).setEchoChar((char) 0);// Hide echo char
                    }
                }
            }
        });
    }
}
