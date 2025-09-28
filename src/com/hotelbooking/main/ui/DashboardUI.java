package com.hotelbooking.main.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class DashboardUI extends JFrame {
    private final JPanel mainPanel;
    private final JButton btnDashboard;
    private final JButton btnBookings;
    private final JButton btnRooms;
    private final JButton btnLogout;

    // Maps to store icons for default and selected states
    private final Map<JButton, Icon> defaultIcons = new HashMap<>();
    private final Map<JButton, Icon> selectedIcons = new HashMap<>();

    public DashboardUI(String username) {
        setTitle("Hotel Booking Dashboard - logged in as : " +username );
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container with a layout for the navbar and content area
        JPanel container = new JPanel(new MigLayout("fill, ins 0", "[60!][grow]", "[grow]"));
        container.setBackground(Color.WHITE);
        getContentPane().add(container);

        // --- Left vertical navbar ---
        JPanel navPanel = new JPanel(new MigLayout("wrap 1, fillx, ins 5 0 5 0", "[fill]", "[]10[]10[]push[]"));
        navPanel.setBackground(Color.WHITE);

        // Create icons (black for default, white for selected)
        Icon dashboardDefault = createIcon("⌂", Color.BLACK); // Home symbol
        Icon dashboardSelected = createIcon("\u2302", Color.WHITE);
        Icon bookingsDefault = createIcon("\uD83D\uDCC5", Color.BLACK); // Calendar symbol
        Icon bookingsSelected = createIcon("\uD83D\uDCC5", Color.WHITE);
        Icon roomsDefault = createIcon("\uD83D\uDECF", Color.BLACK); // Bed symbol
        Icon roomsSelected = createIcon("\uD83D\uDECF", Color.WHITE);
        Icon logoutDefault = createIcon("\u27A1", Color.BLACK); // Arrow/Exit symbol
        Icon logoutSelected = createIcon("\u27A1", Color.WHITE);

        btnDashboard = createNavButton(dashboardDefault, dashboardSelected);
        btnBookings = createNavButton(bookingsDefault, bookingsSelected);
        btnRooms = createNavButton(roomsDefault, roomsSelected);
        btnLogout = createNavButton(logoutDefault, logoutSelected);

        navPanel.add(btnDashboard, "h 50!, w 50!");
        navPanel.add(btnBookings, "h 50!, w 50!");
        navPanel.add(btnRooms, "h 50!, w 50!");
        navPanel.add(btnLogout, "h 50!, w 50!");

        // --- Right main content panel ---
        mainPanel = new JPanel(new MigLayout("fill, wrap 1", "[grow]", "[grow]"));
        mainPanel.setBackground(Color.WHITE);


        container.add(navPanel, "growy");
        container.add(mainPanel, "grow");

        // --- Button Actions ---
        btnDashboard.addActionListener(e -> showDashboard());
        btnBookings.addActionListener(e -> showBookings());
        btnRooms.addActionListener(e -> showRooms());
        btnLogout.addActionListener(e -> logout());

        // Set the initial view
        showDashboard();

        setVisible(true);
    }

    private JButton createNavButton(Icon defaultIcon, Icon selectedIcon) {
        JButton button = new JButton(defaultIcon);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);

        defaultIcons.put(button, defaultIcon);
        selectedIcons.put(button, selectedIcon);

        return button;
    }

    private JLabel createPageLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setForeground(Color.BLACK);
        return label;
    }

    // --- View display methods ---

    /**
     * Populates the main panel with a modern dashboard view.
     * This view includes KPI cards, quick action buttons, and daily activity lists.
     */
    private void showDashboard() {
        // 1. Clear the main panel and set the layout
        mainPanel.removeAll();
        // Layout: wrap after each component, fill available width, add gaps
        mainPanel.setLayout(new MigLayout("wrap 1, fillx, insets 20", "[grow]"));

        // 2. Add a prominent title
        JLabel lblTitle = new JLabel("Dashboard Overview");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.BLACK);
        mainPanel.add(lblTitle, "gapbottom 15");

        // 3. Create a panel for the Key Performance Indicator (KPI) cards
        JPanel kpiPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow][grow]"));
        kpiPanel.setBackground(Color.WHITE);

        // --- Mock Data (replace with real data fetching) ---
        String occupancy = "82%";
        String guestsCheckedIn = "147";
        String roomsAvailable = "18";

        // Add the three KPI cards to their panel
        kpiPanel.add(createKpiCard("Occupancy", occupancy, new Color(39, 174, 96)), "growx, gapright 20");
        kpiPanel.add(createKpiCard("Guests Checked In", guestsCheckedIn, new Color(41, 128, 185)), "growx, gapright 20");
        kpiPanel.add(createKpiCard("Rooms Available", roomsAvailable, new Color(243, 156, 18)), "growx");

        mainPanel.add(kpiPanel, "growx, gapbottom 20");

        // 4. Create a panel for the lower section (Actions and Activity)
        JPanel bottomPanel = new JPanel(new MigLayout("fillx, insets 0", "[40%][60%]"));
        bottomPanel.setBackground(Color.WHITE);

        // Add the "Quick Actions" card
        bottomPanel.add(createActionsCard(), "grow, top, gapright 20");

        // Add the "Today's Activity" card
        bottomPanel.add(createActivityCard(), "grow, top");

        mainPanel.add(bottomPanel, "growx");

        // 5. Refresh the UI and highlight the nav button
        mainPanel.revalidate();
        mainPanel.repaint();
        highlightNav(btnDashboard);
    }

    /**
     * Creates a styled "card" for displaying a key performance indicator (KPI).
     * @param title The title of the metric (e.g., "Occupancy").
     * @param value The value of the metric (e.g., "82%").
     * @param titleColor A color for the title text for visual distinction.
     * @return A styled JPanel representing the KPI card.
     */
    private JPanel createKpiCard(String title, String value, Color titleColor) {
        JPanel card = new JPanel(new MigLayout("wrap 1, fillx", "[center]"));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(224, 224, 224)));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(titleColor);
        card.add(lblTitle, "gapbottom 5, gaptop 15");

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 36));
        lblValue.setForeground(Color.BLACK);
        card.add(lblValue, "gapbottom 15");

        return card;
    }

    /**
     * Creates a card containing quick action buttons.
     * @return A styled JPanel with action buttons.
     */
    private JPanel createActionsCard() {
        JPanel card = new JPanel(new MigLayout("wrap 1, fillx", "[grow]"));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Quick Actions"));

        JButton btnNewBooking = new JButton("Create New Booking");
        JButton btnCheckIn = new JButton("Process Check-In");
        // Add styling and action listeners as needed

        card.add(btnNewBooking, "growx, h 40!, gapbottom 10");
        card.add(btnCheckIn, "growx, h 40!");

        return card;
    }

    /**
     * Creates a card displaying today's arrivals and departures.
     * @return A styled JPanel with activity lists.
     */
    private JPanel createActivityCard() {
        JPanel card = new JPanel(new MigLayout("wrap 2, fill", "[grow][grow]"));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Today's Activity"));

        JLabel lblArrivals = new JLabel("Arrivals (5)");
        lblArrivals.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblDepartures = new JLabel("Departures (3)");
        lblDepartures.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(lblArrivals);
        card.add(lblDepartures);

        // --- Mock Data ---
        JTextArea arrivalsList = new JTextArea("• John Smith (Room 101)\n• Jane Doe (Room 203)\n• Peter Jones (Room 105)\n• Mary Williams (Room 301)\n• David Brown (Room 202)");
        JTextArea departuresList = new JTextArea("• Emily Davis (Room 102)\n• Michael Miller (Room 305)\n• Sarah Wilson (Room 210)");

        // Style the text areas
        styleActivityList(arrivalsList);
        styleActivityList(departuresList);

        card.add(new JScrollPane(arrivalsList), "grow");
        card.add(new JScrollPane(departuresList), "grow");

        return card;
    }

    /**
     * Helper to apply consistent styling to activity list text areas.
     */
    private void styleActivityList(JTextArea textArea) {
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setOpaque(false); // Make it transparent
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    //------------------------------------------------------------------------

    private void showBookings() {
        mainPanel.removeAll();
        mainPanel.add(createPageLabel("Bookings"), "align center");
        mainPanel.revalidate();
        mainPanel.repaint();
        highlightNav(btnBookings);
    }

    private void showRooms() {
        mainPanel.removeAll();
        mainPanel.add(createPageLabel("Rooms"), "align center");
        mainPanel.revalidate();
        mainPanel.repaint();
        highlightNav(btnRooms);
    }

    private void logout() {
        highlightNav(btnLogout);
        JOptionPane.showMessageDialog(this, "Logging out...");
        dispose();
    }

    private void highlightNav(JButton selectedButton) {
        JButton[] buttons = {btnDashboard, btnBookings, btnRooms, btnLogout};
        for (JButton btn : buttons) {
            if (btn == selectedButton) {
                btn.setBackground(Color.BLACK);
                btn.setIcon(selectedIcons.get(btn));
            } else {
                btn.setBackground(Color.WHITE);
                btn.setIcon(defaultIcons.get(btn));
            }
        }
    }

    /**
     * Helper method to generate an Icon from a String (to support emoji).
     * Note: Requires a font that supports the chosen symbols, like "Segoe UI Emoji".
     */
    private static Icon createIcon(String text, Color color) {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // "Segoe UI Emoji" is great for colored emoji on Windows.
        // "Segoe UI Symbol" is a fallback for monochrome symbols.
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