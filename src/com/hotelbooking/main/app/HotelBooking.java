package com.hotelbooking.main.app;

import com.hotelbooking.main.ui.LoginUI;
import javax.swing.*;

public class HotelBooking {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
        });
    }
}
