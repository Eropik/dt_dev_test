package com.vizor.test.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface ImageService {
    BufferedImage resizeImage(File imageFile, double scale) throws IOException;
    BufferedImage createThumbnail(File file) throws IOException;
}
