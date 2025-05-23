package org.emojify;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Emojifier {
    private final static int TILE_SIZE = 10; // pixel size of tile "emoji" (this shouldn't change)
    private final static int MARGIN_SIZE = 5; // the size of the margin white gap between tiles

    /**
     * Turns an image into a BufferedImage color tiles.
     */
    public static BufferedImage emojify(BufferedImage image) {
        int sampleSize = 10; // averaging sample size

        BufferedImage emojified = new BufferedImage(image.getWidth() / sampleSize * (TILE_SIZE + MARGIN_SIZE), image.getHeight() / sampleSize * (TILE_SIZE + MARGIN_SIZE), BufferedImage.TYPE_INT_RGB);

        emojified = ImageHelper.createTransparent(emojified);

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

                int color = new Color(avR, avG, avB).getRGB();

                // now fill in output image with the average color
                for (int x = 0; x < TILE_SIZE; x++) {
                    for (int y = 0; y < TILE_SIZE; y++) {
                        emojified.setRGB(j * (TILE_SIZE + MARGIN_SIZE) + x, i * (TILE_SIZE + MARGIN_SIZE) + y, color);
                    }
                }
            }

        }

        return emojified;
    }
}
