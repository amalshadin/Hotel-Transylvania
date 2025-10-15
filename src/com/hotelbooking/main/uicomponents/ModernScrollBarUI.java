package com.hotelbooking.main.uicomponents;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ModernScrollBarUI extends BasicScrollBarUI {

    private final int THUMB_BORDER_SIZE = 2;
    private final int THUMB_ARC = 10;
    private final Color THUMB_COLOR = new Color(169, 169, 169); // #A9A9A9
    private final Color TRACK_COLOR = new Color(240, 240, 240); // #F0F0F0

    @Override
    protected void configureScrollBarColors() {
        this.trackColor = TRACK_COLOR;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        super.paintTrack(g, c, trackBounds);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the color and draw the rounded thumb
        g2.setColor(THUMB_COLOR);
        g2.fillRoundRect(
                thumbBounds.x + THUMB_BORDER_SIZE,
                thumbBounds.y + THUMB_BORDER_SIZE,
                thumbBounds.width - (THUMB_BORDER_SIZE * 2),
                thumbBounds.height - (THUMB_BORDER_SIZE * 2),
                THUMB_ARC,
                THUMB_ARC
        );

        g2.dispose();
    }

    // --- Remove the Increase/Decrease Arrow Buttons ---

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }
}
