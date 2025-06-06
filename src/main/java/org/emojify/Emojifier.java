package org.emojify;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Emojifier {
    private final static int TILE_SIZE = 10; // pixel size of tile "emoji" (this shouldn't change)
    private final static int MARGIN_SIZE = 3; // the size of the margin black gap between tiles

    private final static int RGB_COLOR_SPACING = 55; // increase this to make it cooler

    private final static ArrayList<Color> COLORS = new ArrayList<>();

    static {
        // restricted amount of total colors
        for (int r = 0; r <= 255; r += RGB_COLOR_SPACING) {
            for (int g = 0; g <= 255; g += RGB_COLOR_SPACING) {
                for (int b = 0; b <= 255; b += RGB_COLOR_SPACING) {
                    COLORS.add(new Color(r, g, b));
                }
            }
        }
    }

    private static Color getClosestColor(Color in) {
        double smallestDist = Double.MAX_VALUE;
        Color closestColor = Color.WHITE;

        for (Color color : COLORS) {
            double dist = Math.sqrt(
                    Math.pow(color.getRed() - in.getRed(), 2) + Math.pow(color.getGreen() - in.getGreen(), 2) + Math.pow(color.getBlue() - in.getBlue(), 2)
            );

            if (dist < smallestDist) {
                smallestDist = dist;

                closestColor = color;
            }
        }

        return closestColor;
    }

    /**
     * Turns an image into a BufferedImage color tiles.
     */
    public static BufferedImage emojify(BufferedImage image, int maxWidth, int maxHeight) {
        int sampleSize = 1; // averaging sample size (start of with highest sample size)

        // find a good sample size
        while (true) {
            int pixelWidth = image.getWidth() / sampleSize * (TILE_SIZE + MARGIN_SIZE);
            int pixelHeight = image.getHeight() / sampleSize * (TILE_SIZE + MARGIN_SIZE);

            // keep on increasing sample size until the emojified image fits the constraints
            if (pixelWidth <= maxWidth && pixelHeight <= maxHeight) {
                break;
            }

            sampleSize ++;
        }

        BufferedImage emojified = new BufferedImage(image.getWidth() / sampleSize * (TILE_SIZE + MARGIN_SIZE), image.getHeight() / sampleSize * (TILE_SIZE + MARGIN_SIZE), BufferedImage.TYPE_INT_RGB);

        emojified = ImageHelper.setBlack(emojified);

        for (int i = 0; sampleSize <= image.getHeight() - i * sampleSize; i++) {
            for (int j = 0; sampleSize <= image.getWidth() - j * sampleSize; j++) {
                int avR = 0;
                int avG = 0;
                int avB = 0;

                // find average rgb within that sampleSize * sampleSize square
                for (int x = 0; x < sampleSize; x++) {
                    for (int y = 0; y < sampleSize; y++) {
                        Color tile = new Color(image.getRGB(j * sampleSize + x, i * sampleSize + y));

                        avR += tile.getRed();
                        avB += tile.getBlue();
                        avG += tile.getGreen();
                    }
                }

                avR /= sampleSize * sampleSize;
                avG /= sampleSize * sampleSize;
                avB /= sampleSize * sampleSize;

                Color closestColor = getClosestColor(new Color(avR, avG, avB));

                // now fill in output image with the average color
                for (int x = 0; x < TILE_SIZE; x++) {
                    for (int y = 0; y < TILE_SIZE; y++) {
                        emojified.setRGB(j * (TILE_SIZE + MARGIN_SIZE) + x, i * (TILE_SIZE + MARGIN_SIZE) + y, closestColor.getRGB());
                    }
                }
            }

        }

        return emojified;
    }
}
