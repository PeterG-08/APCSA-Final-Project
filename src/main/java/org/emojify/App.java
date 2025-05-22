package org.emojify;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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

    private void setCurrentPanel(Panel panel) {
        CardLayout cardLayout = (CardLayout) (mainPanel.getLayout());
        
        cardLayout.show(mainPanel, panel.toString());

        currentPanel = panel;
    }

    private void createPanelThread(Runnable toRun, Panel panel) {
        // image processing thread
        new Thread(() -> {
            while (true) {
                try {
                    if (currentPanel != panel) {
                        Thread.sleep(100); // idle

                        continue;
                    }

                    toRun.run();

                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
        }).start();
    }

    private JPanel buildHomePanel() {
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

    private JPanel buildVideoPanel() {
        JPanel panel = new JPanel();

        JButton toHome = new JButton("⬛🟩🟥");

        toHome.addActionListener(_ -> { setCurrentPanel(Panel.HOME); new Thread(() -> webcam.close()).start(); });

        JTextArea emojis = new JTextArea(10, 10);

        // Display emojis directly in the JTextArea
        String emojiString = "⬛🟩🟥😀";
        emojis.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        emojis.setText(emojiString);
        // Set font to one that supports emojis, like Segoe UI Emoji on Windows
            emojis.setForeground(Color.BLACK);
            emojis.setBackground(Color.WHITE);
            emojis.setOpaque(true);
            // No further code needed: if your system font supports color emojis (like Segoe UI Emoji on Windows 10/11), 
            // and your Java version is 9+, the JTextArea should display color emojis. 
            // If you still see black and white, try updating your Java version or running on a newer OS.


        panel.add(new JLabel("Video"));
        panel.add(toHome);
        panel.add(new JLabel("🟦"));

        panel.add(emojis);

        createPanelThread(() -> {
            if (webcam.isOpen() && webcam.getImage() != null) {
                BufferedImage image = ImageHelper.mirror(webcam.getImage());

                // SwingUtilities.invokeLater(() -> {
                //     emojis.setText(Emojifier.emojify(image));
                // });
            }
        }, Panel.VIDEO);

        return panel;
    }

    private JPanel buildImagePanel() {
        JPanel panel = new JPanel();

        JButton toHome = new JButton("To Home");
        JButton loadImage = new JButton("Load Image");

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));
        fileChooser.addActionListener(e -> {
            if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
                BufferedImage image = null;

                try {
                    image = ImageIO.read(fileChooser.getSelectedFile());
                } catch (IOException err) {
                    err.printStackTrace();
                }

                System.out.println(Emojifier.emojify(image));
            }
        });

        toHome.addActionListener(_ -> setCurrentPanel(Panel.HOME));
        loadImage.addActionListener(_ -> fileChooser.showOpenDialog(loadImage));

        panel.add(new JLabel("Image"));
        panel.add(toHome);
        panel.add(loadImage);

        return panel;
    }
}
