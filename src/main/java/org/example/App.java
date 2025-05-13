package org.example;

import javax.swing.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.*;

public class App {
    private final JPanel mainPanel;

    private final String HOME = "HOME";
    private final String VIDEO = "VIDEO";

    private final JPanel homePanel;
    private final JPanel videoPanel;

    public App() {
        JFrame window = new JFrame("Emojify");

        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new CardLayout());

        homePanel = buildHomePanel();
        videoPanel = buildVideoPanel();

        mainPanel.add(homePanel, HOME);
        mainPanel.add(videoPanel, VIDEO);

        window.add(mainPanel);

        window.setVisible(true);
    }

    private final JPanel buildHomePanel() {
        JPanel panel = new JPanel();

        JButton toVideo = new JButton("To Video!");

        CardLayout c = (CardLayout) (mainPanel.getLayout());

        toVideo.addActionListener(_ -> c.show(mainPanel, VIDEO));

        panel.add(new JLabel("Home"));
        panel.add(toVideo);

        return panel;
    }

    private final JPanel buildVideoPanel() {
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        JPanel panel = new JPanel();

        JButton toHome = new JButton("To Home");

        CardLayout c = (CardLayout) (mainPanel.getLayout());

        toHome.addActionListener(_ -> c.show(mainPanel, HOME));

        WebcamPanel webcamPanel = new WebcamPanel(webcam);

        webcamPanel.setMirrored(true);
        webcamPanel.setImageSizeDisplayed(true);

        panel.add(webcamPanel);
        panel.add(toHome);

        return panel;
    }
}
