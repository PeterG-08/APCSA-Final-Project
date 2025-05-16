package org.emojify;

import javax.swing.*;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;

public class App {
    private final JPanel mainPanel;

    private final Webcam webcam;

    private enum Panels {
        HOME,
        VIDEO,
        IMAGE;
    }

    private Panels currentPanel = Panels.HOME;

    public App() {
        // webcam config
        webcam = Webcam.getDefault();

        JFrame window = new JFrame("Emojify!");

        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new CardLayout());

        mainPanel.add(buildHomePanel(), Panels.HOME.toString());
        mainPanel.add(buildVideoPanel(), Panels.VIDEO.toString());
        mainPanel.add(buildImagePanel(), Panels.IMAGE.toString());

        window.add(mainPanel);

        window.setVisible(true);
    }

    private final void setCurrentPanel(Panels panel) {
        CardLayout cardLayout = (CardLayout) (mainPanel.getLayout());
        
        cardLayout.show(mainPanel, panel.toString());

        currentPanel = panel;
    }

    private final JPanel buildHomePanel() {
        JPanel panel = new JPanel();

        JButton toVideo = new JButton("To Video!");
        JButton toImage = new JButton("To Image!");

        toVideo.addActionListener(_ -> { setCurrentPanel(Panels.VIDEO); new Thread(() -> webcam.open()).start(); });
        toImage.addActionListener(_ -> setCurrentPanel(Panels.IMAGE));

        panel.add(new JLabel("Home"));

        panel.add(toVideo);
        panel.add(toImage);

        return panel;
    }

    private final JPanel buildVideoPanel() {
        JPanel panel = new JPanel();

        JButton toHome = new JButton("To Home");

        toHome.addActionListener(_ -> { setCurrentPanel(Panels.HOME); new Thread(() -> webcam.close()).start(); });

        panel.add(new JLabel("Video"));
        panel.add(toHome);

        // image processing thread
        new Thread(() -> {
            while (true) {
                try {
                    if (currentPanel != Panels.VIDEO) {
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

        toHome.addActionListener(_ -> setCurrentPanel(Panels.HOME));

        panel.add(new JLabel("Image"));
        panel.add(toHome);

        return panel;
    }
}
