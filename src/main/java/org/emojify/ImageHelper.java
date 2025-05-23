package org.emojify;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageHelper {
    public static BufferedImage mirror(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null); // draw original image to new image

        AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
        transform.translate(-newImage.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        return op.filter(newImage, null);
    }

    public static BufferedImage createTransparent(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = newImage.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setColor(new Color(0, 0, 0, 230));
        g.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        g.dispose();

	    return newImage;
    }
}
