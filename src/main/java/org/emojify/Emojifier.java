package org.emojify;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Emojifier {
    /**
     * Turns an image into a String of tile emojis.
     */
    public static String emojify(BufferedImage image) {
        for(int i = 0; i<=image.getHeight();i+= 10){
            for(int j = 0; j<= image.getWidth(); j+=10){
                int avR = 0;
                int avG = 0;
                int avB = 0;
                for(int x = 0; x <=10; x++){
                    for(int y = 0; y<=10; y++){
                        Color tile = new Color(image.getRGB(i+x, j+y));
                        avR+= tile.getRed();
                        avB+= tile.getBlue();
                        avG+= tile.getGreen();
                    }
                }
                avR/=100;
                avG/=100;
                avB/=100;
            }

        }
        return "⬛⬛⬛⬛⬛⬛";
    }
}
