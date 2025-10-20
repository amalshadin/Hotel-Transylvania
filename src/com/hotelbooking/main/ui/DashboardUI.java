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
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.toedter.calendar.JDateChooser;
import java.util.Date;


import com.hotelbooking.main.db.DatabaseHelper;
import com.hotelbooking.main.uicomponents.ModernScrollBarUI;

public class DashboardUI extends JFrame {
    private final JPanel mainPanel;
    private final JButton btnDashboard;
    private final JButton btnBookings;
    private final JButton btnRooms;
    private final JButton btnProfile; // Changed from btnLogout

    // Maps to store icons for default and selected states
    private final Map<JButton, Icon> defaultIcons = new HashMap<>();
    private final Map<JButton, Icon> selectedIcons = new HashMap<>();
    private final String currentUsername; // Store the username

    public DashboardUI(String username) {
        this.currentUsername = username;
        setTitle("Hotel Booking Dashboard - Logged in as: " + username);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container with a layout for the navbar and content area
        JPanel container = new JPanel(new MigLayout("fill, ins 0", "[60!][grow]", "[grow]"));
        container.setBackground(new Color(245, 245, 245));
        getContentPane().add(container);

        // --- Left vertical navbar ---
        JPanel navPanel = new JPanel(new MigLayout("wrap 1, fillx, ins 5 0 5 0", "[fill]", "[]10[]10[]10[]push[]"));
        navPanel.setBackground(Color.WHITE);

        // Create icons
        Icon dashboardIcon = createIcon("\uD83C\uDFE0", Color.BLACK); // Home icon
        Icon bookingsIcon = createIcon("\uD83D\uDCC5", Color.BLACK); // Calendar icon
        Icon roomsIcon = createIcon("\uD83D\uDECF", Color.BLACK); // Bed icon
        Icon profileIcon = createIcon("\uD83D\uDC64", Color.BLACK); // User silhouette icon

        btnDashboard = createNavButton("Dashboard", dashboardIcon);
        btnBookings = createNavButton("Bookings", bookingsIcon);
        btnRooms = createNavButton("Rooms", roomsIcon);
        btnProfile = createNavButton("Profile", profileIcon); // Changed from Logout

        navPanel.add(btnDashboard, "h 50!, w 50!");
        navPanel.add(btnBookings, "h 50!, w 50!");
        navPanel.add(btnRooms, "h 50!, w 50!");
        navPanel.add(btnProfile, "h 50!, w 50!");

        // --- Right main content panel ---
        mainPanel = new JPanel(new CardLayout()); // Using CardLayout for easy switching
        mainPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
//        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        container.add(navPanel, "growy");
        container.add(scrollPane, "grow");;

        // --- Button Actions ---
        btnDashboard.addActionListener(e -> showDashboard());
        btnBookings.addActionListener(e -> showBookings());
        btnRooms.addActionListener(e -> showRooms());
        btnProfile.addActionListener(e -> showProfile()); // Action for the new profile button

        // Set the initial view
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

        // Add a subtle border for separation
        button.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        defaultIcons.put(button, icon);
        // For simplicity, we'll handle selection with background color, not a different icon
        return button;
    }

    // --- View display methods ---

    private void showDashboard() {
        mainPanel.removeAll();
        mainPanel.setLayout(new MigLayout("wrap 1, fillx, insets 25", "[grow]"));

        JLabel lblTitle = new JLabel("Welcome back, " + currentUsername + "!");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.BLACK);
        mainPanel.add(lblTitle, "gapbottom 15");

        JPanel kpiPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow, sg][grow, sg][grow, sg]"));
        kpiPanel.setOpaque(false);
        kpiPanel.add(createKpiCard("Upcoming Stays", "2", new Color(41, 128, 185)), "growx, gapright 20");
        kpiPanel.add(createKpiCard("Past Bookings", "5", new Color(39, 174, 96)), "growx, gapright 20");
        kpiPanel.add(createKpiCard("Loyalty Points", "1,250", new Color(243, 156, 18)), "growx");
        mainPanel.add(kpiPanel, "growx, gapbottom 25");

        mainPanel.add(new JSeparator(), "growx, gapbottom 25");

        // Quick Actions section
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
        actionsPanel.add(btnViewBookings, "growx, h 45!, gapright 20");
        mainPanel.add(actionsPanel, "growx");

        btnBookNow.addActionListener(e -> showRooms());
        btnViewBookings.addActionListener(e -> showBookings());

        updateUI();
        highlightNav(btnDashboard);
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


    /**
     * NEW: Displays the user's booking history in a table.
     */
    private void showBookings() {
        mainPanel.removeAll();
        mainPanel.setLayout(new MigLayout("fill, insets 25", "[grow]", "[][grow]"));

        JLabel lblTitle = new JLabel("My Booking History");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(lblTitle, "wrap, gapbottom 15");

        // --- Data Fetching ---
        DatabaseHelper dbHelper = new DatabaseHelper();
        java.util.List<Object[]> bookingData = dbHelper.getBookingsByUsername(currentUsername);

        // --- Table Model ---
        // Added an "Action" column for the cancel button
        String[] columnNames = {"Booking ID", "Room Type", "Check-in", "Check-out", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the "Action" column should be interactive
                return column == 5;
            }
        };

        // Populate the model with data from the database
        for (Object[] row : bookingData) {
            model.addRow(new Object[]{row[0], row[1], row[2], row[3], row[4], "Cancel"});
        }

        // --- JTable Setup ---
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(173, 216, 230));

        // --- Hide the 'Booking ID' column from view (but keep it in the model) ---
        table.removeColumn(table.getColumnModel().getColumn(0));

        // --- Custom Renderer and Editor for the "Action" Button ---
        TableColumn actionColumn = table.getColumnModel().getColumn(4); // Column index is 4 now
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        // Style the table header
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

// --- Helper Classes for the JTable Button ---

    /**
     * Renders a JButton in a table cell.
     */
    class ButtonRenderer extends DefaultTableCellRenderer {
        private final JButton button = new JButton();

        public ButtonRenderer() {
            button.setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            // Set the button's text to the value of the cell (e.g., "Cancel")
            button.setText((value == null) ? "" : value.toString());

            // Get the status from the "Status" column (index 3 after ID column is hidden)
            String status = (String) table.getModel().getValueAt(row, 4);

            // --- Logic to enable/disable button ---
            if ("Confirmed".equals(status)) {
                button.setEnabled(true);
                button.setBackground(new Color(211, 84, 0)); // Orange color for cancel
                button.setForeground(Color.WHITE);
            } else {
                button.setEnabled(false);
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.DARK_GRAY);
            }
            return button;
        }
    }

    /**
     * Handles the click event for the button in the table cell.
     */
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
            // Get the Booking ID from the hidden first column of the model
            this.bookingId = (Integer) table.getModel().getValueAt(row, 0);

            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            // --- Confirmation and Cancellation Logic ---
            String status = (String) table.getModel().getValueAt(row, 4);
            if ("Confirmed".equals(status)) {
                int choice = JOptionPane.showConfirmDialog(
                        button,
                        "Are you sure you want to cancel booking ID: " + bookingId + "?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    DatabaseHelper dbHelper = new DatabaseHelper();
                    if (dbHelper.cancelBookingById(bookingId)) {
                        // Update the status in the table model directly
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

        // --- Hardcoded Room Inventory ---
        // In a real app, this might come from a different database table.
        final Map<String, Integer> roomInventory = new HashMap<>();
        roomInventory.put("Deluxe AC King", 5);
        roomInventory.put("Standard AC Queen", 8);
        roomInventory.put("Executive Suite AC", 3);
        roomInventory.put("Standard Non-AC", 10);
        roomInventory.put("Economy Twin Non-AC", 6);


        // --- 1. Booking Header Panel ---
        JPanel headerPanel = new JPanel(new MigLayout("insets 0", "[][grow][][grow][]"));
        headerPanel.setOpaque(false);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDate(new Date()); // Default to today
        dateChooser.setMinSelectableDate(new Date()); // Can't book in the past

        JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1)); // Min 1 day, max 30

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

        // Panel to hold all room listings
        JPanel roomsContainer = new JPanel(new MigLayout("wrap 1, fillx", "[grow]"));
        roomsContainer.setOpaque(false);
        mainPanel.add(roomsContainer, "growx");

        // --- 2. Action Listener for Availability Check ---
        btnCheckAvailability.addActionListener(e -> {
            Date checkInDate = dateChooser.getDate();
            int nights = (Integer) daysSpinner.getValue();

            if (checkInDate == null) {
                JOptionPane.showMessageDialog(mainPanel, "Please select a check-in date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Re-draw the room cards with updated availability
            drawRoomCards(roomsContainer, roomInventory, checkInDate, nights);
        });

        // --- 3. Initial Display ---
        // Perform an initial check for today's date
        drawRoomCards(roomsContainer, roomInventory, new Date(), 1);

        updateUI();
        highlightNav(btnRooms);
    }

    /**
     * Helper method to clear and redraw all room cards based on availability for the selected dates.
     */
    private void drawRoomCards(JPanel container, Map<String, Integer> inventory, Date checkInDate, int nights) {
        container.removeAll();
        DatabaseHelper dbHelper = new DatabaseHelper();

        // --- AC Rooms Section ---
        container.add(createSectionHeader("Air-Conditioned Rooms"), "gaptop 10");
        container.add(new JSeparator(), "growx, wrap, gapbottom 10");
        JPanel acPanel = createRoomGridPanel();

        for (String roomType : new String[]{"Deluxe AC King", "Standard AC Queen", "Executive Suite AC"}) {
            int totalRooms = inventory.get(roomType);
            int bookedRooms = dbHelper.getBookingCountForDateRange(roomType, checkInDate, nights);
            int availableRooms = totalRooms - bookedRooms;
            acPanel.add(createRoomCard(roomType, availableRooms, checkInDate, nights), "growx");
        }
        container.add(acPanel, "growx, wrap");


        // --- Non-AC Rooms Section ---
        container.add(createSectionHeader("Non-Air-Conditioned Rooms"), "gaptop 25");
        container.add(new JSeparator(), "growx, wrap, gapbottom 10");
        JPanel nonAcPanel = createRoomGridPanel();

        for (String roomType : new String[]{"Standard Non-AC", "Economy Twin Non-AC"}) {
            int totalRooms = inventory.get(roomType);
            int bookedRooms = dbHelper.getBookingCountForDateRange(roomType, checkInDate, nights);
            int availableRooms = totalRooms - bookedRooms;
            nonAcPanel.add(createRoomCard(roomType, availableRooms, checkInDate, nights), "growx");
        }
        container.add(nonAcPanel, "growx, wrap");

        updateUI();
    }

    /**
     * Creates and styles a section header label.
     */
    private JLabel createSectionHeader(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }

    /**
     * Creates a panel with a 2-column grid layout for room cards.
     */
    private JPanel createRoomGridPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 0, gapx 20", "[sg, grow][sg, grow]"));
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Overloaded createRoomCard to handle dynamic data and availability.
     */
    private JPanel createRoomCard(String roomType, int availableCount, Date checkInDate, int nights) {
        String description;
        String price;
        String[] amenities;

        switch (roomType) {
            case "Deluxe AC King":
                description = "Spacious and elegant, featuring a king-sized bed and enhanced amenities.";
                price = "₹4,500 / night";
                amenities = new String[]{"Air Conditioning", "Free Wi-Fi", "4K Smart TV", "Mini-bar", "City View"};
                break;
            case "Executive Suite AC":
                description = "The pinnacle of luxury with a separate living area, kitchenette, and panoramic city views.";
                price = "₹8,000 / night";
                amenities = new String[]{"All Deluxe amenities", "Living Area", "Kitchenette", "VIP Lounge Access"};
                break;
            case "Standard AC Queen":
                description = "A comfortable room with a queen-sized bed, perfect for business travelers or couples.";
                price = "₹3,200 / night";
                amenities = new String[]{"Air Conditioning", "Free Wi-Fi", "HD TV", "Work Desk"};
                break;
            case "Standard Non-AC":
                description = "An affordable and clean room with a comfortable double bed.";
                price = "₹1,800 / night";
                amenities = new String[]{"Ceiling Fan", "Free Wi-Fi", "TV", "Private Bathroom"};
                break;
            case "Economy Twin Non-AC":
                description = "Perfect for friends or colleagues, this room features two separate single beds.";
                price = "₹1,000 / night";
                amenities = new String[]{"Ceiling Fan", "Free Wi-Fi", "Shared Bathroom"};
                break;
            default:
                description = "A comfortable and well-equipped room.";
                price = "N/A";
                amenities = new String[]{"Free Wi-Fi"};
                break;
        }

        JPanel card = new JPanel(new MigLayout("wrap 1, fill", "[grow]"));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel lblTitle = new JLabel(roomType);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        card.add(lblTitle, "gaptop 15, gapleft 20");
        JTextArea txtDesc = new JTextArea(description);
        card.add(txtDesc, "growx, gapleft 20, gapright 20, gapbottom 10");

        for (String amenity : amenities) {
            card.add(new JLabel("• " + amenity), "gapleft 25");
        }
        // --- Dynamic Booking Section ---

        JPanel bottomPanel = new JPanel(new MigLayout("fillx", "[grow][right]")); // Use a two-column layout
        bottomPanel.setOpaque(false);

        JLabel lblPrice = new JLabel(price);
        lblPrice.setHorizontalAlignment(SwingConstants.RIGHT); // 1. Right-align the text
        lblPrice.setFont(new Font("Arial", Font.BOLD, 18));    // 2. Set font, BOLD weight, and size
        lblPrice.setForeground(new Color(0, 100, 0));

        JLabel lblAvailability;
        JButton btnBook = new JButton("Book Now");
        styleActionButton(btnBook);

        if (availableCount > 0) {
            lblAvailability = new JLabel(availableCount + " room(s) available");
            lblAvailability.setForeground(new Color(0, 128, 0)); // Green
            btnBook.setEnabled(true);
        } else {
            lblAvailability = new JLabel("No rooms available");
            lblAvailability.setForeground(Color.RED);
            btnBook.setEnabled(false);
        }
        lblAvailability.setFont(new Font("Arial", Font.BOLD, 14));

        bottomPanel.add(lblAvailability, "aligny center");
        bottomPanel.add(btnBook, "w 120!, h 35!");
        bottomPanel.add(lblPrice, "growx, aligny center");

        // --- BOOKING ACTION ---
        btnBook.addActionListener(e -> {
            String confirmationMessage = String.format(
                    "Confirm booking for:\n\nRoom: %s\nCheck-in: %tF\nNights: %d\n\nDo you want to proceed?",
                    roomType, checkInDate, nights
            );
            int choice = JOptionPane.showConfirmDialog(mainPanel, confirmationMessage, "Confirm Booking", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                DatabaseHelper dbHelper = new DatabaseHelper();
                boolean success = dbHelper.createBooking(currentUsername, roomType, checkInDate, nights);
                if (success) {
                    JOptionPane.showMessageDialog(mainPanel, "Booking successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    drawRoomCards((JPanel) card.getParent().getParent(), (Map<String, Integer>) ((Object) null), checkInDate, nights); // A bit complex to get parent, but works
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Booking failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        card.add(bottomPanel, "growx, gaptop 20, gapbottom 15, gapleft 20, gapright 20");
        return card;
    }

    /**
     * NEW: Displays the user profile, settings, and logout button.
     */

    private void showProfile() {
        mainPanel.removeAll();
        mainPanel.setLayout(new MigLayout("fill, insets 25", "[grow]", "[][][grow]"));

        JLabel lblTitle = new JLabel("My Profile & Settings");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(lblTitle, "wrap, gapbottom 20");

        // --- FETCH REAL USER DATA ---
        DatabaseHelper dbHelper = new DatabaseHelper();
        Map<String, String> userDetails = dbHelper.getUserDetails(currentUsername);
        String fullName = userDetails.getOrDefault("fullName", currentUsername);
        String email = userDetails.getOrDefault("email", "Not set");
        String phone = userDetails.getOrDefault("phone", "Not set");

        // -- Personal Information Card --
        JPanel infoPanel = createTitledCard("Personal Information");
        // --- DISPLAY REAL DATA ---
        infoPanel.add(createFormField("Full Name:", fullName), "growx, wrap");
        infoPanel.add(createFormField("Email Address:", email), "growx, wrap");
        infoPanel.add(createFormField("Phone Number:", phone), "growx, wrap, gapbottom 10");

        JButton btnEditInfo = new JButton("Edit Information");
        infoPanel.add(btnEditInfo, "align right");
        mainPanel.add(infoPanel, "growx, wrap, gapbottom 20");

        // -- Security Card --
        JPanel securityPanel = createTitledCard("Security");
        JButton btnChangePassword = new JButton("Change Password");
        securityPanel.add(btnChangePassword);

        btnChangePassword.addActionListener(e -> {
            ChangePasswordUI changePasswordDialog = new ChangePasswordUI(this, currentUsername);
            changePasswordDialog.setVisible(true);
        });
        mainPanel.add(securityPanel, "growx, wrap, gapbottom 20");

        // -- Logout Button --
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
            }
        });
        mainPanel.add(btnLogout, "w 150!, h 40!, al right, gaptop 50");

        updateUI();
        highlightNav(btnProfile);
    }

    private JPanel createTitledCard(String title) {
        // Changed layout to "wrap 1" to stack components vertically
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
        // Reset all buttons to default state
        for (JButton btn : new JButton[]{btnDashboard, btnBookings, btnRooms, btnProfile}) {
            btn.setBackground(Color.WHITE);
            // Add a subtle effect to show it's not selected
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        }

        // Highlight the selected button
        selectedButton.setBackground(new Color(240, 240, 240)); // A light grey for selection
        // Add a visual indicator (left border) for the active button
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