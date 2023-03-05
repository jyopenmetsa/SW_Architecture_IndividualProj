package org.example.server.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Operations {

    public BufferedImage toGrayscale(BufferedImage inputImage){

        //To grayscale operation
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(inputImage.getRGB(x, y));
                int gray = (int) (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue());
                Color grayColor = new Color(gray, gray, gray);
                inputImage.setRGB(x, y, grayColor.getRGB());
            }
        }

        return inputImage;
    }

    public BufferedImage toResize(BufferedImage inputImage, int newWidth, int newHeight){

        // Resize the image with new width and height
        Image tmp = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return outputImage;
    }

    public BufferedImage toRotate(BufferedImage inputImage, String direction) throws IOException {

        // Rotate the image
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = outputImage.createGraphics();
        if (direction == "right") {
            g2d.rotate(Math.toRadians(90));
            g2d.translate(0, -height);
        } else {
            g2d.rotate(Math.toRadians(-90));
            g2d.translate(-width, 0);
        }
        g2d.drawImage(inputImage, 0, 0, null);
        g2d.dispose();

        return outputImage;
    }

    public BufferedImage toRotateAngle(BufferedImage inputImage, int rotationAngle) throws IOException {

        // Rotate the image
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = outputImage.createGraphics();
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(rotationAngle), width / 2, height / 2);
        g2d.setTransform(transform);
        g2d.drawImage(inputImage, 0, 0, null);
        g2d.dispose();

        return outputImage;
    }

    public BufferedImage toFlip(BufferedImage inputImage, String flipDirection) throws IOException {

        // Flip the image
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = outputImage.createGraphics();
        if (flipDirection.equals("horizontal")) {
            g2d.drawImage(inputImage, width, 0, -width, height, null);
        } else {
            System.out.println("vertical");
            g2d.drawImage(inputImage, 0, height, width, -height, null);
        }
        g2d.dispose();

        return outputImage;
    }

    public BufferedImage toThumbnail(BufferedImage inputImage) throws IOException {

        // Set the thumbnail dimensions
        int thumbWidth = 300;
        int thumbHeight = 300;

        // Generate the thumbnail image
        BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = thumbImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(inputImage, 0, 0, thumbWidth, thumbHeight, null);
        g2d.dispose();

        return thumbImage;
    }
}
