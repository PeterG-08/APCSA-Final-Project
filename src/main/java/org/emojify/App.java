package org.emojify;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // full screen, and prevent resizing logic
        window.setResizable(false); 

        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setMinimumSize(screenSize);

        // add in main container panel
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

                    Thread.sleep(50);
                } catch (InterruptedException e) {}
            }
        }).start();
    }

    private JPanel buildHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        JButton videoButton = new JButton("Video");
        JButton imageButton = new JButton("Image");

        videoButton.addActionListener(_ -> {
            setCurrentPanel(Panel.VIDEO);
            new Thread(() -> webcam.open()).start();
        });

        imageButton.addActionListener(_ -> setCurrentPanel(Panel.IMAGE));

        // layout
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

        gbc.gridx = 0;
        panel.add(videoButton, gbc);

        gbc.gridx = 1;
        panel.add(imageButton, gbc);

        return panel;
    }

    private JPanel buildVideoPanel() {
        JPanel panel = new JPanel();

        JButton toHome = new JButton("To Home");

        JLabel emojifiedVideo = new JLabel();

        toHome.addActionListener(_ -> { setCurrentPanel(Panel.HOME); new Thread(() -> webcam.close()).start(); });

        createPanelThread(() -> {
            BufferedImage webcamImage = webcam.getImage();

            if (webcam.isOpen() && webcamImage != null) {
                BufferedImage mirroredImage = ImageHelper.mirror(webcamImage);

                SwingUtilities.invokeLater(() -> {
                    emojifiedVideo.setIcon(new ImageIcon(Emojifier.emojify(mirroredImage, (int) (mainPanel.getWidth() / 1.2), (int) (mainPanel.getHeight() / 1.2))));
                    emojifiedVideo.repaint();
                });
            }
        }, Panel.VIDEO);

        // layout
        panel.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel videoLabel = new JLabel("Video");
        videoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        videoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(videoLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(emojifiedVideo, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(toHome, gbc);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildImagePanel() {
        JPanel panel = new JPanel();

        JButton toHome = new JButton("To Home");
        JButton loadImage = new JButton("Load Image");
        JButton saveImage = new JButton("Save Image");

        JLabel emojifiedImage = new JLabel();

        JFileChooser fileChooser = new JFileChooser();
        JFileChooser saveChooser = new JFileChooser();

        saveChooser.setDialogTitle("Save Image");
        fileChooser.setDialogTitle("Load Image");

        saveChooser.setApproveButtonText("Ok");

        saveChooser.setFileFilter(new FileNameExtensionFilter("Image Files (.png)", "png"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));

        fileChooser.addActionListener(e -> {
            if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
                try {
                    BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                    
                    if (image != null) {
                        SwingUtilities.invokeLater(() -> {
                            emojifiedImage.setIcon(new ImageIcon(Emojifier.emojify(image, (int) (mainPanel.getWidth() / 1.2), (int) (mainPanel.getHeight() / 1.2))));
                            emojifiedImage.repaint();
                        });
                    }

                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        });

        saveChooser.addActionListener(e -> {
            Icon icon = emojifiedImage.getIcon();

            if (!(icon instanceof ImageIcon)) {
                return;
            }

            BufferedImage savedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
            );

            // draw emojified icon to saved image
            Graphics2D g2d = savedImage.createGraphics();
            icon.paintIcon(null, g2d, 0, 0);
            g2d.dispose();


            if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {

                try {
                    String path = saveChooser.getSelectedFile().getAbsolutePath() + ".png";

                    ImageIO.write(savedImage, "png", new File(path));
                } 
                
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        toHome.addActionListener(_ -> setCurrentPanel(Panel.HOME));
        loadImage.addActionListener(_ -> fileChooser.showOpenDialog(loadImage));
        saveImage.addActionListener(_ -> saveChooser.showOpenDialog(saveImage));

        // layout
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel imageLabel = new JLabel("Image");
        imageLabel.setFont(new Font("Arial", Font.BOLD, 24));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(imageLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 10, 20, 10);
        panel.add(emojifiedImage, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 10, 10, 10);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(toHome);
        buttonPanel.add(loadImage);
        buttonPanel.add(saveImage);

        panel.add(buttonPanel, gbc);

        return panel;
    }
}
