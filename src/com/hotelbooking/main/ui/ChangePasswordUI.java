package com.hotelbooking.main.ui;

import com.hotelbooking.main.db.DatabaseHelper; // You'll create this next
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordUI extends JDialog {

    private final JPasswordField pfCurrentPassword;
    private final JPasswordField pfNewPassword;
    private final JPasswordField pfConfirmPassword;
    private final String username;

    public ChangePasswordUI(Frame owner, String username) {
        super(owner, "Change Password", true); // true for modal
        this.username = username;

        setSize(600, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][][grow, fill][]"));

        // Title
        JLabel lblTitle = new JLabel("Update Your Password");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitle, "wrap, center, gapbottom 15");

        // Form Fields
        pfCurrentPassword = createPasswordField("Current Password:");
        pfNewPassword = createPasswordField("New Password:");
        pfConfirmPassword = createPasswordField("Confirm New Password:");

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow, sg][grow, sg]"));
        JButton btnCancel = new JButton("Cancel");
        JButton btnSaveChanges = new JButton("Save Changes");

        stylePrimaryButton(btnSaveChanges);
        buttonPanel.add(btnCancel, "growx, h 40!");
        buttonPanel.add(btnSaveChanges, "growx, h 40!");
        add(buttonPanel, "dock south, gaptop 20");

        // --- Action Listeners ---
        btnCancel.addActionListener(e -> dispose());

        btnSaveChanges.addActionListener(e -> savePasswordChanges());
    }

    private JPasswordField createPasswordField(String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        add(label, "wrap, gaptop 5");

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        add(passwordField, "growx, wrap, gapbottom 10, h 30!");
        return passwordField;
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
    }

    private void savePasswordChanges() {
        String currentPassword = new String(pfCurrentPassword.getPassword());
        String newPassword = new String(pfNewPassword.getPassword());
        String confirmPassword = new String(pfConfirmPassword.getPassword());

        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Database Interaction ---
        DatabaseHelper dbHelper = new DatabaseHelper();
        boolean isCurrentPasswordValid = dbHelper.verifyCurrentUserPassword(username, currentPassword);

        if (!isCurrentPasswordValid) {
            JOptionPane.showMessageDialog(this, "The current password you entered is incorrect.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = dbHelper.changePassword(username, newPassword);

        if (success) {
            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close the dialog on success
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update password. Please try again.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}