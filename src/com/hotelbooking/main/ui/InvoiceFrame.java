package com.hotelbooking.main.ui;

import com.hotelbooking.main.db.DatabaseHelper;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;


import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.sql.Date;


class InvoiceFrame extends JFrame {

    // NEW: Add a field for the dbHelper
    private DatabaseHelper dbHelper;

    // NEW: Update constructor to accept the dbHelper
    public InvoiceFrame(int bookingId, DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;

        // --- Live Database Call ---
        Map<String, Object> invoiceData = dbHelper.getInvoiceDetails(bookingId);

        if (invoiceData == null) {
            JOptionPane.showMessageDialog(this, "Could not retrieve invoice details.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Stop execution if data is not found
        }

        // Retrieve data from the map
        String guestName = (String) invoiceData.get("guestName");
        String roomType = (String) invoiceData.get("roomType");
        Date checkInSql = (Date) invoiceData.get("checkIn");
        Date checkOutSql = (Date) invoiceData.get("checkOut");
        double pricePerNight = (double) invoiceData.get("price");

        // Convert SQL Dates to LocalDate for calculation
        LocalDate checkIn = checkInSql.toLocalDate();
        LocalDate checkOut = checkOutSql.toLocalDate();

        // Calculate nights and totals
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights == 0) nights = 1; // Minimum 1 night charge

        double subtotal = nights * pricePerNight;
        double tax = subtotal * 0.18; // 18% tax
        double total = subtotal + tax;
        // --- End Live Database Call ---


        setTitle("Invoice for Booking #" + bookingId);
        setSize(450, 550);
        // This closes only this window, not the whole app
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // This opens the window in the center of the screen
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new MigLayout("fillx, insets 20", "[left, grow][right, grow]"));
        panel.setBackground(Color.WHITE);
        add(panel);

        // --- Header ---
        JLabel titleLabel = new JLabel("HOTEL INVOICE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        panel.add(titleLabel, "span, center, wrap, gapbottom 20");

        // --- Booking Details ---
        panel.add(new JLabel("Booking ID:"), "gapright 10");
        panel.add(new JLabel(String.valueOf(bookingId)), "wrap");
        panel.add(new JLabel("Guest Name:"), "gapright 10");
        panel.add(new JLabel(guestName), "wrap");
        panel.add(new JLabel("Room Type:"), "gapright 10");
        panel.add(new JLabel(roomType), "wrap");
        panel.add(new JLabel("Check-in Date:"), "gapright 10");
        panel.add(new JLabel(checkIn.toString()), "wrap");
        panel.add(new JLabel("Check-out Date:"), "gapright 10");
        panel.add(new JLabel(checkOut.toString()), "wrap, gapbottom 15");

        // --- Line Items Table ---
        String[] columnNames = {"Description", "Amount"};
        Object[][] data = {
                {"Room Charge (" + nights + " nights)", String.format("₹%.2f", subtotal)},
                {"Taxes & Fees (18%)", String.format("₹%.2f", tax)}
        };
        JTable itemsTable = new JTable(data, columnNames);
        itemsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        itemsTable.setRowHeight(25);
        itemsTable.setEnabled(false); // Make it read-only
        panel.add(new JScrollPane(itemsTable), "span, growx, wrap, gapbottom 15");

        // --- Total ---
        JLabel totalLabel = new JLabel("TOTAL AMOUNT");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(totalLabel, "span, right, wrap");

        JLabel totalValueLabel = new JLabel(String.format("$%.2f", total));
        totalValueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        totalValueLabel.setForeground(new Color(0, 128, 0));
        panel.add(totalValueLabel, "span, right, wrap");
    }
}
