package org.emojify;

import com.github.sarxos.webcam.Webcam;

import javax.swing.*;


import java.awt.*;

public class App {
    private final JPanel mainPanel;

    private final Webcam webcam;

    private enum Panel {
        HOME,
        VIDEO,
        IMAGE;
    }

    private Panel currentPanel = Panel.HOME;

    public App() {
        // webcam config
        webcam = Webcam.getDefault();

        JFrame window = new JFrame("Emojify!");

        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new CardLayout());

        mainPanel.add(buildHomePanel(), Panel.HOME.toString());
        mainPanel.add(buildVideoPanel(), Panel.VIDEO.toString());
        mainPanel.add(buildImagePanel(), Panel.IMAGE.toString());

        window.add(mainPanel);

        window.setVisible(true);
    }

    private final void setCurrentPanel(Panel panel) {
        CardLayout cardLayout = (CardLayout) (mainPanel.getLayout());
        
        cardLayout.show(mainPanel, panel.toString());

        currentPanel = panel;
    }

    private final JPanel buildHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel homeLabel = new JLabel("Home");

        homeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        panel.add(homeLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton videoButton = new JButton("Video");
        JButton imageButton = new JButton("Image");

        gbc.gridx = 0;
        panel.add(videoButton, gbc);

        gbc.gridx = 1;
        panel.add(imageButton, gbc);

        videoButton.addActionListener(_ -> {
            setCurrentPanel(Panel.VIDEO);
            new Thread(() -> webcam.open()).start();
        });

        imageButton.addActionListener(_ -> setCurrentPanel(Panel.IMAGE));

        return panel;
    }

    private final JPanel buildVideoPanel() {
        JPanel panel = new JPanel();

        JButton toHome = new JButton("To Home");

        toHome.addActionListener(_ -> { setCurrentPanel(Panel.HOME); new Thread(() -> webcam.close()).start(); });

        panel.add(new JLabel("Video"));
        panel.add(toHome);

        // image processing thread
        new Thread(() -> {
            while (true) {
                try {
                    if (currentPanel != Panel.VIDEO) {
                        Thread.sleep(100); // idle

                        continue;
                    }

                    if (webcam.isOpen()) {
                        // use the emojify function here
                    }

                    Thread.sleep(20);
                } catch (InterruptedException e) {}
            }
        }).start();

        return panel;
    }

    private final JPanel buildImagePanel() {
        JPanel panel = new JPanel();

        JButton toHome = new JButton("To Home");

        toHome.addActionListener(_ -> setCurrentPanel(Panel.HOME));

        panel.add(new JLabel("Image"));
        panel.add(toHome);

        return panel;
    }
}
