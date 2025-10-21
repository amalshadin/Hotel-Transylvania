package com.hotelbooking.main.ui;

import com.hotelbooking.main.db.DatabaseHelper;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardUI extends JFrame {

    private final DatabaseHelper dbHelper;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    public AdminDashboardUI() {
        this.dbHelper = new DatabaseHelper();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        setTitle("Admin Control Panel");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(createSidebar(), BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Add different view panels to the CardLayout
        mainPanel.add(createUsersPanel(), "Users");
        mainPanel.add(createBookingsPanel(), "Bookings");
        mainPanel.add(createFaresPanel(), "Fares");

        // Show the Users panel by default
        cardLayout.show(mainPanel, "Users");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new MigLayout("wrap 1, fillx, insets 10", "[grow]"));
        sidebar.setBackground(new Color(45, 52, 54));

        JLabel titleLabel = new JLabel("ADMIN PANEL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        sidebar.add(titleLabel, "gapbottom 30, align center");

        sidebar.add(createNavButton("View Users", "Users"), "growx, h 40!, gapbottom 10");
        sidebar.add(createNavButton("Upcoming Bookings", "Bookings"), "growx, h 40!, gapbottom 10");
        sidebar.add(createNavButton("Manage Room Fares", "Fares"), "growx, h 40!, gapbottom 10");

        JButton logoutButton = new JButton("Logout");
        styleNavButton(logoutButton);
        logoutButton.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
        sidebar.add(logoutButton, "dock south, h 40!, gaptop 20");

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        styleNavButton(button);
        button.addActionListener(e -> cardLayout.show(mainPanel, cardName));
        return button;
    }

    private void styleNavButton(JButton button) {
        button.setBackground(new Color(99, 110, 114));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    private JPanel createCustomPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = createCustomPanel("Registered Users");
        String[] columnNames = {"User ID", "Username", "Full Name", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        dbHelper.getAllUsers().forEach(model::addRow);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = createCustomPanel("All Upcoming Bookings");
        String[] columnNames = {"Booking ID", "User Full Name", "Room Type", "Check-in", "Check-out"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        dbHelper.getAllUpcomingBookings().forEach(model::addRow);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFaresPanel() {
        JPanel panel = createCustomPanel("Manage Room Fares");
        String[] columnNames = {"Room Type", "Price per Night (INR)"};

        // Fetch data and create the table model
        List<Object[]> fareData = dbHelper.getRoomFares();
        Object[][] data = fareData.toArray(new Object[0][]);
        DefaultTableModel model = new DefaultTableModel(data, columnNames);

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            // Stop any active cell editing before saving. This is the crucial part.
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }

            int updatedRows = 0;
            for (int i = 0; i < table.getRowCount(); i++) {
                String roomType = (String) table.getValueAt(i, 0);
                // Add a try-catch block for safety in case of non-numeric input
                try {
                    double newPrice = Double.parseDouble(table.getValueAt(i, 1).toString());
                    if (dbHelper.updateRoomFare(roomType, newPrice)) {
                        updatedRows++;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Invalid price format for room: " + roomType,
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return; // Stop the save process if one value is bad
                }
            }
            JOptionPane.showMessageDialog(panel, "Successfully updated " + updatedRows + " room fares.", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(saveButton, BorderLayout.SOUTH);
        return panel;
    }
}
