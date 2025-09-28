package com.hotelbooking.main.ui;

import com.hotelbooking.main.uicomponents.ImagePanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ForgotPassword extends JFrame {
    public ForgotPassword()  {
        this.setSize(new Dimension(1024, 576));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setTitle("Forgot Password");

        JPanel forgotImagePanel = new ImagePanel("/forgotPassword.png");
        forgotImagePanel.setLayout(new MigLayout("fill", "[60%][40%]","grow"));
        JPanel forgotRightPanel = new JPanel(new MigLayout(
            "fill",
                "[grow]",
                "25%[50%]25%"
        ));
//        JPanel forgotComponentPanel = new JPanel(new MigLayout(
//                "fill,insets 10,wrap 1",
//                "[grow]",
//                ""
//        ));
//        forgotComponentPanel.setBackground(new Color(0xCCCCCC));
//        forgotRightPanel.add(forgotComponentPanel,"cell 1 0");

        forgotImagePanel.add(forgotRightPanel,"cell 0 1");
        this.add(forgotImagePanel);
        forgotImagePanel.setVisible(true);
    }
}
