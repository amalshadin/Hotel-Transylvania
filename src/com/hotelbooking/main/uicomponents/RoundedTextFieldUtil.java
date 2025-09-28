package com.hotelbooking.main.uicomponents;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;

public class RoundedTextFieldUtil {

    // Inner class for rounded border
    static class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color borderColor;

        public RoundedBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    /**
     * Makes a text field rounded.
     * @param textField JTextField (or subclass)
     * @param radius corner radius
     * @param bg background color
     * @param borderColor border color
     */
    public static void makeRounded(JTextField textField, int radius, Color bg, Color borderColor) {
        textField.setOpaque(false); // Needed for custom painting
        textField.setBackground(bg);
        textField.setBorder(new RoundedBorder(radius, borderColor));
    }
}
