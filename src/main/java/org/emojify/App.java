package org.emojify;

import javax.swing.*;

import com.github.sarxos.webcam.Webcam;

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
        JPanel panel = new JPanel();

        JButton toVideo = new JButton("To Video!");
        JButton toImage = new JButton("To Image!");

        toVideo.addActionListener(_ -> { setCurrentPanel(Panel.VIDEO); new Thread(() -> webcam.open()).start(); });
        toImage.addActionListener(_ -> setCurrentPanel(Panel.IMAGE));

        panel.add(new JLabel("Home"));

        panel.add(toVideo);
        panel.add(toImage);

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
