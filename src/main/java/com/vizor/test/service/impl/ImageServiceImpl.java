package com.vizor.test.service.impl;

import com.vizor.test.service.ImageService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageServiceImpl implements ImageService {

    @Override
    public BufferedImage resizeImage(File imageFile, double scale) throws IOException {
        BufferedImage img = ImageIO.read(imageFile);
        int width = (int) (img.getWidth() / scale);
        int height = (int) (img.getHeight() / scale);
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    @Override
    public BufferedImage createThumbnail(File file) throws IOException {
        return resizeImage(file, 2.0);
    }
}