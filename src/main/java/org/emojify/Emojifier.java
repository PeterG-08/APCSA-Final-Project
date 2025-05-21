package org.emojify;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Emojifier {
    private final static int TILE_PIXELS = 10;

    private final static HashMap<int[], String> emojiMap = new HashMap<>();

    static {
        emojiMap.put(new int[]{248, 49, 47}, "🟥");
        emojiMap.put(new int[]{255, 103, 35}, "🟧");
        emojiMap.put(new int[]{255, 176, 46}, "🟨");
        emojiMap.put(new int[]{0, 210, 106}, "🟩");
        emojiMap.put(new int[]{0, 166, 237}, "🟦");
        emojiMap.put(new int[]{199, 144, 241}, "🟪");
        emojiMap.put(new int[]{165, 105, 83}, "🟫");
        emojiMap.put(new int[]{0, 0, 0}, "⬜");
        emojiMap.put(new int[]{255, 255, 255}, "⬛");
    }

    /**
     * Turns an image into a String of tile emojis.
     */
    public static String emojify(BufferedImage image) {
        String out = "";

        System.out.println(image.getHeight());

        for(int i = 0; i < image.getHeight() / TILE_PIXELS; i += 1) {
            for(int j = 0; j < image.getWidth() / TILE_PIXELS; j += 1) {
                int avR = 0;
                int avG = 0;
                int avB = 0;

                for(int x = 0; x < TILE_PIXELS - 1; x++) {
                    for(int y = 0; y < TILE_PIXELS - 1; y++) {
                        Color tile = new Color(image.getRGB(i*TILE_PIXELS+x, j*TILE_PIXELS+y));

                        avR += tile.getRed();
                        avB += tile.getBlue();
                        avG += tile.getGreen();
                    }
                }

                avR /= TILE_PIXELS * TILE_PIXELS;
                avG /= TILE_PIXELS * TILE_PIXELS;
                avB /= TILE_PIXELS * TILE_PIXELS;

                double smallestDist = Double.MAX_VALUE;
                String closestTile = "⬜";

                for (int[] color : emojiMap.keySet()) {
                    double dist = Math.sqrt(
                            Math.pow(color[0] - avR, 2) + Math.pow(color[1] - avG, 2) + Math.pow(color[2] - avB, 2)
                    );

                    if (dist < smallestDist) {
                        smallestDist = dist;

                        closestTile = emojiMap.get(color);
                    }
                }

                out += closestTile;
            }

            out += "\n";
        }

        return out;
    }
}
