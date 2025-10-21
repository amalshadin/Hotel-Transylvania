package com.hotelbooking.main.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.util.Date;


import com.hotelbooking.main.db.DatabaseHelper;
import com.hotelbooking.main.uicomponents.ModernScrollBarUI;

public class DashboardUI extends JFrame {
    private final JPanel mainPanel;
    private final JButton btnDashboard;
    private final JButton btnBookings;
    private final JButton btnRooms;
    private final JButton btnProfile;

    private final Map<JButton, Icon> defaultIcons = new HashMap<>();
    private final String currentUsername;
    private final DatabaseHelper dbHelper;

    public DashboardUI(String username) {
        this.currentUsername = username;
        this.dbHelper = new DatabaseHelper();
        setTitle("Hotel Booking Dashboard - Logged in as: " + username);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel container = new JPanel(new MigLayout("fill, ins 0", "[60!][grow]", "[grow]"));
        container.setBackground(new Color(245, 245, 245));
        getContentPane().add(container);

        JPanel navPanel = new JPanel(new MigLayout("wrap 1, fillx, ins 5 0 5 0", "[fill]", "[]10[]10[]10[]push[]"));
        navPanel.setBackground(Color.WHITE);

        Icon dashboardIcon = createIcon("\uD83C\uDFE0", Color.BLACK);
        Icon bookingsIcon = createIcon("\uD83D\uDCC5", Color.BLACK);
        Icon roomsIcon = createIcon("\uD83D\uDECF", Color.BLACK);
        Icon profileIcon = createIcon("\uD83D\uDC64", Color.BLACK);

        btnDashboard = createNavButton("Dashboard", dashboardIcon);
        btnBookings = createNavButton("Bookings", bookingsIcon);
        btnRooms = createNavButton("Rooms", roomsIcon);
        btnProfile = createNavButton("Profile", profileIcon);

        navPanel.add(btnDashboard, "h 50!, w 50!");
        navPanel.add(btnBookings, "h 50!, w 50!");
        navPanel.add(btnRooms, "h 50!, w 50!");
        navPanel.add(btnProfile, "h 50!, w 50!");

        mainPanel = new JPanel(new CardLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        container.add(navPanel, "growy");
        container.add(scrollPane, "grow");

        btnDashboard.addActionListener(e -> showDashboard());
        btnBookings.addActionListener(e -> showBookings());
        btnRooms.addActionListener(e -> showRooms());
        btnProfile.addActionListener(e -> showProfile());

        showDashboard();
        setVisible(true);
    }

    private JButton createNavButton(String toolTip, Icon icon) {
        JButton button = new JButton(icon);
        button.setToolTipText(toolTip);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setBackground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        defaultIcons.put(button, icon);
        return button;
    }

    private void showDashboard() {
        List<Map<String, Object>> allRooms = dbHelper.getAllRoomTypes();
        int upcomingStays = dbHelper.getUpcomingBookingsCount(currentUsername);
        int pastBookings = dbHelper.getPastBookingsCount(currentUsername);
        Map<String, Integer> bookedToday = dbHelper.getBookedRoomsCountForToday();

        int totalInventory = allRooms.stream().mapToInt(room -> (int) room.get("total_inventory")).sum();
        int totalBooked = bookedToday.values().stream().mapToInt(Integer::intValue).sum();
        int roomsAvailable = totalInventory - totalBooked;

        mainPanel.removeAll();
        mainPanel.setLayout(new MigLayout("wrap 1, fillx, insets 25", "[grow]"));
        mainPanel.setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel("Welcome back, " + currentUsername + "!");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(lblTitle, "gapbottom 15");

        JPanel kpiPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow, sg][grow, sg][grow, sg]"));
        kpiPanel.setOpaque(false);
        kpiPanel.add(createKpiCard("Rooms Available Today", String.valueOf(roomsAvailable), new Color(39, 174, 96)), "growx, gapright 20");
        kpiPanel.add(createKpiCard("Upcoming Stays", String.valueOf(upcomingStays), new Color(41, 128, 185)), "growx, gapright 20");
        kpiPanel.add(createKpiCard("Past Bookings", String.valueOf(pastBookings), new Color(142, 68, 173)), "growx");
        mainPanel.add(kpiPanel, "growx, gapbottom 25");

        mainPanel.add(createAvailabilityPanel(bookedToday, allRooms), "growx, gapbottom 25");

        JLabel lblActions = new JLabel("Quick Actions");
        lblActions.setFont(new Font("Arial", Font.BOLD, 22));
        mainPanel.add(lblActions, "gapbottom 10");

        JPanel actionsPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow, sg][grow, sg][grow, sg]"));
        actionsPanel.setOpaque(false);
        JButton btnBookNow = new JButton("Book a New Room");
        JButton btnViewBookings = new JButton("View My Bookings");
        styleActionButton(btnBookNow);
        styleActionButton(btnViewBookings);
        actionsPanel.add(btnBookNow, "growx, h 45!, gapright 20");
        actionsPanel.add(btnViewBookings, "growx, h 45!");
        mainPanel.add(actionsPanel, "growx");

        btnBookNow.addActionListener(e -> showRooms());
        btnViewBookings.addActionListener(e -> showBookings());

        updateUI();
        highlightNav(btnDashboard);
    }

    private JPanel createAvailabilityPanel(Map<String, Integer> bookedTodayMap, List<Map<String, Object>> allRooms) {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 20", "[grow 30][grow 70]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));

        JLabel lblSectionTitle = new JLabel("Today's Room Availability");
        lblSectionTitle.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(lblSectionTitle, "span, gapbottom 15");

        for (Map<String, Object> room : allRooms) {
            String roomType = (String) room.get("type_name");
            int totalCount = (int) room.get("total_inventory");
            int bookedCount = bookedTodayMap.getOrDefault(roomType, 0);
            int availableCount = totalCount - bookedCount;

            JLabel lblRoomType = new JLabel(roomType);
            lblRoomType.setFont(new Font("Arial", Font.PLAIN, 14));
            panel.add(lblRoomType, "growx");

            JPanel statusPanel = new JPanel(new MigLayout("insets 0, fill"));
            statusPanel.setOpaque(false);

            JLabel lblStatusText = new JLabel(String.format("%d / %d Available", availableCount, totalCount));
            lblStatusText.setFont(new Font("Arial", Font.BOLD, 14));
            statusPanel.add(lblStatusText, "split 2, gapright 10, align right");

            JLabel statusIndicator = new JLabel("●");
            statusIndicator.setFont(new Font("Arial", Font.BOLD, 24));
            if (availableCount == 0) {
                statusIndicator.setForeground(new Color(231, 76, 60)); // Red
            } else if ((double) availableCount / totalCount <= 0.25) {
                statusIndicator.setForeground(new Color(243, 156, 18)); // Orange
            } else {
                statusIndicator.setForeground(new Color(39, 174, 96)); // Green
            }
            statusPanel.add(statusIndicator, "align left");
            panel.add(statusPanel, "growx, wrap");
        }
        return panel;
    }

    private JPanel createKpiCard(String title, String value, Color titleColor) {
        JPanel card = new JPanel(new MigLayout("wrap 1, fill", "[left]"));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);
        card.add(lblTitle, "gapbottom 5");

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 32));
        lblValue.setForeground(titleColor);
        card.add(lblValue);

        return card;
    }

    private void styleActionButton(JButton button) {
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showBookings() {
        mainPanel.removeAll();
        mainPanel.setLayout(new MigLayout("fill, insets 25", "[grow]", "[][grow]"));

        JLabel lblTitle = new JLabel("My Booking History");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(lblTitle, "wrap, gapbottom 15");

        // MODIFICATION: Added "View Invoice" to column names
        String[] columnNames = {"Booking ID", "Room Type", "Check-in", "Check-out", "Status", "Cancel Booking", "View Invoice"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // MODIFICATION: Make both action columns clickable
                return column == 5 || column == 6;
            }
        };

        List<Object[]> bookingData = dbHelper.getBookingsByUsername(currentUsername);
        for (Object[] row : bookingData) {
            // MODIFICATION: Add text for the new buttons in each row
            model.addRow(new Object[]{row[0], row[1], row[2], row[3], row[4], "Cancel", "Show Invoice"});
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(173, 216, 230));

        // This hides the Booking ID column from view but keeps its data in the model
        table.removeColumn(table.getColumnModel().getColumn(0));

        // --- Button Column Setup ---
        TableColumn cancelColumn = table.getColumnModel().getColumn(4);
        cancelColumn.setCellRenderer(new ButtonRenderer());
        cancelColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        // NEW: Setup for the "Show Invoice" button (Column 5)
        TableColumn invoiceColumn = table.getColumnModel().getColumn(5);
        invoiceColumn.setCellRenderer(new InvoiceButtonRenderer());
        invoiceColumn.setCellEditor(new InvoiceButtonEditor(new JCheckBox(), dbHelper));
        // --- End Button Column Setup ---


        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        mainPanel.add(scrollPane, "grow");

        updateUI();
        highlightNav(btnBookings);
    }
    class InvoiceButtonRenderer extends DefaultTableCellRenderer {
        private final JButton button = new JButton();
        public InvoiceButtonRenderer() { button.setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            button.setText((value == null) ? "" : value.toString());
            String status = (String) table.getModel().getValueAt(row, 4);
            if ("Confirmed".equals(status)) { // Or whatever status allows viewing an invoice
                button.setEnabled(true);
                button.setBackground(new Color(0, 128, 0)); // Green color for invoice
                button.setForeground(Color.WHITE);
            } else {
                button.setEnabled(false);
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.DARK_GRAY);
            }
            return button;
        }
    }
    class InvoiceButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private int bookingId;
        private JTable table;
        private int row;
        private DatabaseHelper dbHelper; // NEW: Add a field for the dbHelper

        // NEW: Update constructor to accept the dbHelper
        public InvoiceButtonEditor(JCheckBox checkBox, DatabaseHelper dbHelper) {
            super(checkBox);
            this.dbHelper = dbHelper; // Store the dbHelper instance
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.table = table;
            this.row = row;
            this.bookingId = (Integer) table.getModel().getValueAt(row, 0);
            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            String status = (String) table.getModel().getValueAt(row, 4);
            if ("Confirmed".equals(status)) {
                // NEW: Pass the dbHelper to the InvoiceFrame
                InvoiceFrame invoiceFrame = new InvoiceFrame(bookingId, dbHelper);
                invoiceFrame.setVisible(true);
            }
            return "Show Invoice";
        }
    }

    class ButtonRenderer extends DefaultTableCellRenderer {
        private final JButton button = new JButton();
        public ButtonRenderer() { button.setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            button.setText((value == null) ? "" : value.toString());
            String status = (String) table.getModel().getValueAt(row, 4);
            if ("Confirmed".equals(status)) {
                button.setEnabled(true);
                button.setBackground(new Color(211, 84, 0));
                button.setForeground(Color.WHITE);
            } else {
                button.setEnabled(false);
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.DARK_GRAY);
            }
            return button;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private int bookingId;
        private JTable table;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.table = table;
            this.row = row;
            this.bookingId = (Integer) table.getModel().getValueAt(row, 0);
            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            String status = (String) table.getModel().getValueAt(row, 4);
            if ("Confirmed".equals(status)) {
                int choice = JOptionPane.showConfirmDialog(
                        button, "Are you sure you want to cancel booking ID: " + bookingId + "?",
                        "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    if (dbHelper.cancelBookingById(bookingId)) {
                        table.getModel().setValueAt("Canceled", row, 4);
                    } else {
                        JOptionPane.showMessageDialog(button, "Failed to cancel booking.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            return button.getText();
        }
    }

    private void showRooms() {
        mainPanel.removeAll();
        mainPanel.setLayout(new MigLayout("wrap 1, fillx, insets 25", "[grow]", "[]"));

        JPanel headerPanel = new JPanel(new MigLayout("insets 0", "[][grow][][grow][]"));
        headerPanel.setOpaque(false);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDate(new Date());
        dateChooser.setMinSelectableDate(new Date());
        JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));

        headerPanel.add(new JLabel("Check-in Date:"));
        headerPanel.add(dateChooser, "w 150!");
        headerPanel.add(new JLabel("Number of Nights:"), "gapleft 20");
        headerPanel.add(daysSpinner, "w 60!");

        JButton btnCheckAvailability = new JButton("Search");
        btnCheckAvailability.setFocusPainted(false);
        btnCheckAvailability.setBackground(new Color(60, 179, 113));
        headerPanel.add(btnCheckAvailability, "gapleft 20, h 30!");
        mainPanel.add(headerPanel, "growx, wrap, gapbottom 15");

        JLabel lblInfo = new JLabel("Check-in is at 2:00 PM. Check-out is at 11:00 AM the next day.");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInfo.setForeground(Color.GRAY);
        mainPanel.add(lblInfo, "wrap, gapbottom 20");

        JPanel roomsContainer = new JPanel(new MigLayout("wrap 1, fillx", "[grow]"));
        roomsContainer.setOpaque(false);
        mainPanel.add(roomsContainer, "growx");

        btnCheckAvailability.addActionListener(e -> {
            Date checkInDate = dateChooser.getDate();
            int nights = (Integer) daysSpinner.getValue();
            if (checkInDate == null) {
                JOptionPane.showMessageDialog(mainPanel, "Please select a check-in date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            drawRoomCards(roomsContainer, checkInDate, nights);
        });

        drawRoomCards(roomsContainer, new Date(), 1);
        updateUI();
        highlightNav(btnRooms);
    }

    private void drawRoomCards(JPanel container, Date checkInDate, int nights) {
        container.removeAll();
        List<Map<String, Object>> allRooms = dbHelper.getAllRoomTypes();

        // --- AC Rooms Section ---
        container.add(createSectionHeader("Air-Conditioned Rooms"), "gaptop 10");
        container.add(new JSeparator(), "growx, wrap, gapbottom 10");
        JPanel acPanel = createRoomGridPanel();
        allRooms.stream()
                .filter(room -> {
                    String name = (String) room.get("type_name");
                    // This is the corrected filter logic
                    return name.contains("AC") && !name.contains("Non-AC");
                })
                .forEach(room -> {
                    int totalRooms = (int) room.get("total_inventory");
                    int bookedRooms = dbHelper.getBookingCountForDateRange((String) room.get("type_name"), checkInDate, nights);
                    acPanel.add(createRoomCard(room, totalRooms - bookedRooms, checkInDate, nights), "growx");
                });
        container.add(acPanel, "growx, wrap");

        // --- Non-AC Rooms Section ---
        container.add(createSectionHeader("Non-Air-Conditioned Rooms"), "gaptop 25");
        container.add(new JSeparator(), "growx, wrap, gapbottom 10");
        JPanel nonAcPanel = createRoomGridPanel();
        allRooms.stream()
                .filter(room -> ((String) room.get("type_name")).contains("Non-AC")) // This filter was already correct
                .forEach(room -> {
                    int totalRooms = (int) room.get("total_inventory");
                    int bookedRooms = dbHelper.getBookingCountForDateRange((String) room.get("type_name"), checkInDate, nights);
                    nonAcPanel.add(createRoomCard(room, totalRooms - bookedRooms, checkInDate, nights), "growx");
                });
        container.add(nonAcPanel, "growx, wrap");

        updateUI();
    }

    private JLabel createSectionHeader(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }

    private JPanel createRoomGridPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 0, gapx 20", "[sg, grow][sg, grow]"));
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createRoomCard(Map<String, Object> roomData, int availableCount, Date checkInDate, int nights) {
        String roomType = (String) roomData.get("type_name");
        String description = (String) roomData.get("description");
        String price = "₹" + ((BigDecimal) roomData.get("price_per_night")).toPlainString() + " / night";
        String[] amenities = ((String) roomData.get("amenities")).split(",");

        JPanel card = new JPanel(new MigLayout("wrap 1, fill", "[grow]"));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel lblTitle = new JLabel(roomType);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        card.add(lblTitle, "gaptop 15, gapleft 20");

        JTextArea txtDesc = new JTextArea(description);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setLineWrap(true);
        txtDesc.setOpaque(false);
        txtDesc.setEditable(false);
        txtDesc.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDesc.setForeground(Color.DARK_GRAY);
        card.add(txtDesc, "growx, gapleft 20, gapright 20, gapbottom 10");

        for (String amenity : amenities) {
            if (!amenity.trim().isEmpty()) {
                card.add(new JLabel("• " + amenity.trim()), "gapleft 25");
            }
        }

        JPanel bottomPanel = new JPanel(new MigLayout("fillx", "[grow]push[center][right]"));
        bottomPanel.setOpaque(false);

        JLabel lblPrice = new JLabel(price);
        lblPrice.setFont(new Font("Arial", Font.BOLD, 18));
        lblPrice.setForeground(new Color(0, 100, 0));

        JLabel lblAvailability;
        JButton btnBook = new JButton("Book Now");
        styleActionButton(btnBook);

        if (availableCount > 0) {
            lblAvailability = new JLabel(availableCount + " room(s) available");
            lblAvailability.setForeground(new Color(0, 128, 0));
            btnBook.setEnabled(true);
        } else {
            lblAvailability = new JLabel("No rooms available");
            lblAvailability.setForeground(Color.RED);
            btnBook.setEnabled(false);
        }
        lblAvailability.setFont(new Font("Arial", Font.BOLD, 14));

        bottomPanel.add(lblAvailability, "aligny center");
        bottomPanel.add(lblPrice, "aligny center, gapright 15");
        bottomPanel.add(btnBook, "w 120!, h 35!");

        btnBook.addActionListener(e -> {
            String confirmationMessage = String.format(
                    "Confirm booking for:\n\nRoom: %s\nCheck-in: %tF\nNights: %d\n\nDo you want to proceed?",
                    roomType, checkInDate, nights
            );
            int choice = JOptionPane.showConfirmDialog(mainPanel, confirmationMessage, "Confirm Booking", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                boolean success = dbHelper.createBooking(currentUsername, roomType, checkInDate, nights);
                if (success) {
                    JOptionPane.showMessageDialog(mainPanel, "Booking successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    showRooms();
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Booking failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        card.add(bottomPanel, "growx, gaptop 20, gapbottom 15, gapleft 20, gapright 20");
        return card;
    }

    private void showProfile() {
        mainPanel.removeAll();
        mainPanel.setLayout(new MigLayout("fill, insets 25", "[grow]", "[][][grow]"));

        JLabel lblTitle = new JLabel("My Profile & Settings");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(lblTitle, "wrap, gapbottom 20");

        Map<String, String> userDetails = dbHelper.getUserDetails(currentUsername);
        String fullName = userDetails.getOrDefault("fullName", currentUsername);
        String phone = userDetails.getOrDefault("phone", "Not set");

        JPanel infoPanel = createTitledCard("Personal Information");
        infoPanel.add(createFormField("Full Name:", fullName), "growx, wrap");
        infoPanel.add(createFormField("Username:", currentUsername), "growx, wrap");
        infoPanel.add(createFormField("Phone Number:", phone), "growx, wrap, gapbottom 10");

        JButton btnEditInfo = new JButton("Edit Information");
        infoPanel.add(btnEditInfo, "align right");
        mainPanel.add(infoPanel, "growx, wrap, gapbottom 20");

        JPanel securityPanel = createTitledCard("Security");
        JButton btnChangePassword = new JButton("Change Password");
        securityPanel.add(btnChangePassword);

        btnChangePassword.addActionListener(e -> {
            ChangePasswordUI changePasswordDialog = new ChangePasswordUI(this, currentUsername);
            changePasswordDialog.setVisible(true);
        });
        mainPanel.add(securityPanel, "growx, wrap, gapbottom 20");

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                new LoginUI().setVisible(true);
                dispose();
            }
        });
        mainPanel.add(btnLogout, "w 150!, h 40!, al right, gaptop 50");

        updateUI();
        highlightNav(btnProfile);
    }

    private JPanel createTitledCard(String title) {
        JPanel card = new JPanel(new MigLayout("wrap 1, fillx", "[grow]"));
        card.setBackground(Color.WHITE);
        Border margin = new EmptyBorder(15, 15, 15, 15);
        Border titled = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), title);
        card.setBorder(BorderFactory.createCompoundBorder(titled, margin));
        return card;
    }

    private JPanel createFormField(String labelText, String valueText) {
        JPanel panel = new JPanel(new MigLayout("fill, ins 0", "[120!][grow]"));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField textField = new JTextField(valueText);
        textField.setEditable(false);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        textField.setOpaque(false);
        panel.add(label, "ay top");
        panel.add(textField, "growx");
        return panel;
    }

    private void updateUI() {
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void highlightNav(JButton selectedButton) {
        for (JButton btn : new JButton[]{btnDashboard, btnBookings, btnRooms, btnProfile}) {
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        }
        selectedButton.setBackground(new Color(240, 240, 240));
        selectedButton.setBorder(BorderFactory.createMatteBorder(0, 4, 1, 0, new Color(52, 152, 219)));
    }

    private static Icon createIcon(String text, Color color) {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();
        int x = (image.getWidth() - fm.stringWidth(text)) / 2;
        int y = ((image.getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, x, y);
        g2.dispose();
        return new ImageIcon(image);
    }
}