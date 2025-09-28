package com.hotelbooking.main.uicomponents;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * A JPanel that displays a background image, scaled to fit its bounds.
 */
public class ImagePanel extends JPanel {

    private Image backgroundImage;

    public ImagePanel(String imagePath) {
        // Load the image
        try {
            // A more robust way to get resources from the classpath
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                // If the resource is not found, throw an exception with a helpful message
                throw new IllegalArgumentException("Image not found at path: " + imagePath);
            }
            this.backgroundImage = new ImageIcon(imageUrl).getImage();
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            e.printStackTrace();
            this.backgroundImage = null; // Ensure backgroundImage is null if loading fails
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image, scaled to fit the panel's current size
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}